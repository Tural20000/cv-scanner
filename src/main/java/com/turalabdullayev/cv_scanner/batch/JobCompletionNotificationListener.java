package com.turalabdullayev.cv_scanner.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	private final JavaMailSender mailSender;

	public JobCompletionNotificationListener(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! BATCH JOB BİTDİ! İndi HR-a bildiriş göndərilir...");

			try {
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo("hr_team@example.com");
				message.setSubject("CV Scanner Job Hesabatı - Uğurlu");
				message.setText("Salam HR Komandası,\n\n" + "Yüklədiyiniz CV paketi uğurla emal edildi.\n" + "Job ID: "
						+ jobExecution.getJobId() + "\n" + "Başlama vaxtı: " + jobExecution.getStartTime() + "\n"
						+ "Bitmə vaxtı: " + jobExecution.getEndTime() + "\n\n"
						+ "Sistem daxilindən namizədləri filterləyə bilərsiniz.");

				mailSender.send(message);
				log.info("Bildiriş maili uğurla göndərildi.");
			} catch (Exception e) {
				log.error(
						"Mail göndərilərkən xəta baş verdi (SMTP ayarları qoşulmayıbsa normaldır): " + e.getMessage());
			}
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			log.error("!!! BATCH JOB UĞURSUZ OLDU! Xətaları yoxlayın.");
		}
	}
}