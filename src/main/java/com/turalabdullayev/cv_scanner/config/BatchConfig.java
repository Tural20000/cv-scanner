package com.turalabdullayev.cv_scanner.config;

import java.io.File;
import java.io.IOException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import com.turalabdullayev.cv_scanner.batch.CvItemProcessor;
import com.turalabdullayev.cv_scanner.model.Candidate;
import com.turalabdullayev.cv_scanner.repository.CandidateRepository;

@Configuration
public class BatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final CandidateRepository candidateRepository;
	private final CvItemProcessor cvItemProcessor;

	public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			CandidateRepository candidateRepository, CvItemProcessor cvItemProcessor) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.candidateRepository = candidateRepository;
		this.cvItemProcessor = cvItemProcessor;
	}

	@Bean
	@StepScope
	public ItemReader<Resource> cvResourceReader(@Value("#{jobParameters['cvFolder']}") String cvFolderPath)
			throws IOException {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		final Resource[] resources = resolver.getResources("file:" + cvFolderPath + "/*.*");

		return new ItemReader<Resource>() {
			private int index = 0;

			@Override
			public Resource read() {
				while (index < resources.length) {
					Resource currentResource = resources[index++];
					String filename = currentResource.getFilename();

					if (filename != null && !filename.startsWith(".")) {
						return currentResource;
					}
				}
				return null;
			}
		};
	}

	@Bean
	public RepositoryItemWriter<Candidate> candidateWriter() {
		return new RepositoryItemWriterBuilder<Candidate>().repository(candidateRepository).methodName("save").build();
	}

	@Bean
	public Step cvProcessStep(ItemReader<Resource> reader) {
		return new StepBuilder("cvProcessStep", jobRepository).<Resource, Candidate>chunk(10, transactionManager)
				.reader(reader).processor(resource -> {
					File file = resource.getFile();
					if (file.isDirectory()) {
						return null;
					}
					return cvItemProcessor.process(file);
				}).writer(candidateWriter()).build();
	}

	@Bean
	public Job cvScannerJob(Step cvProcessStep) {
		return new JobBuilder("cvScannerJob", jobRepository).start(cvProcessStep).build();
	}
}