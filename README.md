# 🚀 CV Scanner & Parser API

Bu layihə, HR mütəxəssislərinin işini asanlaşdırmaq üçün hazırlanmış avtomatlaşdırılmış CV analiz sistemidir. Sistem daxilində çoxlu sayda namizəd CV-si (PDF, DOCX) olan ZIP arxivlərini qəbul edir, asinxron şəkildə tərkibini oxuyur və məlumatları verilənlər bazasına köçürür.

## 🛠 Texnologiyalar
* **Java 21** & **Spring Boot 3.4.2**
* **Spring Batch** (Böyük həcmli dataların hissə-hissə, təhlükəsiz işlənməsi üçün)
* **Apache Tika** (PDF və Word sənədlərindən mətnləri təmiz oxumaq üçün)
* **Apache POI** (Dinamik Excel hesabatları yaratmaq üçün)
* **PostgreSQL** & **Docker Compose**

---

## 🚀 Sistemi Ayağa Qaldırmaq (Cəmi 2 Əmr)

Layihə tamamilə konteynerləşdirilib. Sistemi işə salmaq üçün İDE açmağa ehtiyat yoxdur. Terminalda (CMD) layihənin kök qovluğuna keçib bu iki əmri yazın:

1. Layihənin JAR faylını yığın:
```bash
   mvn clean package -DskipTests
Docker Compose ilə həm bazanı, həm proqramı işə salın:

Bash
   docker compose up --build -d
Tətbiq hazır olduqdan sonra http://localhost:8080 portunda işləyəcək.

📖 API Test Metodları (Postman / Swagger)
Vizual test üçün brauzerdə bu linki açın:

👉 http://localhost:8080/swagger-ui.html

Postman İlə Test Addımları:
CV Yükləmə (POST): http://localhost:8080/api/cv/upload

Body -> form-data seçin. KEY hissəsinə file yazın (tipini File edin), VALUE hissəsinə test ZIP faylını yükləyin.

Cavab olaraq sizə bir Job ID veriləcək.

Status Yoxlama (GET): http://localhost:8080/api/cv/status/{jobId}

Yuxarıdakı ID-ni yazaraq prosesin bitməsini izləyin.

Axtarış və Filter (GET): http://localhost:8080/api/candidates/search

Query Params: skills=Java, minExperience=3, location=Baku

Excel Hesabat (GET): http://localhost:8080/api/candidates/export

Postman-da Send düyməsinin yanındakı oxa sıxıb Send and Download edin. Filterə uyğun təmiz Excel faylı enəcək.