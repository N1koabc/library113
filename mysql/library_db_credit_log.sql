-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: library_db
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `credit_log`
--

DROP TABLE IF EXISTS `credit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `credit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `delta` int NOT NULL COMMENT '增减分数，如 +3, -5',
  `reason` varchar(255) NOT NULL COMMENT '原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credit_log`
--

LOCK TABLES `credit_log` WRITE;
/*!40000 ALTER TABLE `credit_log` DISABLE KEYS */;
INSERT INTO `credit_log` VALUES (1,1031,2,'管理员后台调整','2026-02-19 13:12:27'),(2,1,3,'按时签到','2026-02-19 14:42:54'),(3,1031,3,'按时签到','2026-02-19 14:54:45'),(4,1031,1,'正常签退','2026-02-19 14:55:01'),(5,1031,-5,'超时30分钟未签到自动违约','2026-02-19 15:30:00'),(6,2,-5,'超时30分钟未签到自动违约','2026-02-19 15:30:00'),(7,1,1,'正常签退','2026-02-20 13:15:22'),(8,1,-5,'超时15分钟未签到自动违约','2026-02-20 13:45:00'),(9,1031,3,'按时签到','2026-02-20 14:02:49'),(10,1031,1,'正常签退','2026-02-20 18:11:58'),(11,1031,-5,'超时15分钟未签到自动违约','2026-02-20 18:45:00'),(12,1031,-5,'超时15分钟未签到自动违约','2026-02-20 19:15:00'),(13,1031,3,'按时签到奖励','2026-02-20 19:37:12'),(14,1031,1,'正常签退','2026-02-20 19:38:02'),(15,1031,-5,'超时15分钟未签到自动违约','2026-02-20 20:15:00'),(16,1031,3,'按时签到奖励','2026-02-20 21:24:44'),(17,1031,1,'正常完成自习自动签退','2026-02-21 11:53:00'),(18,1031,3,'按时签到奖励','2026-02-21 12:48:40'),(19,1,-5,'超时15分钟未签到自动违约','2026-02-21 12:55:00'),(20,1031,1,'正常签退','2026-02-21 14:00:53'),(21,1031,-5,'超时15分钟未签到自动违约','2026-02-21 14:15:00'),(22,1031,3,'按时签到奖励','2026-02-21 14:28:51'),(23,1031,1,'正常签退','2026-02-21 14:29:16'),(24,1031,3,'按时签到奖励','2026-02-21 14:40:27'),(25,1031,1,'正常签退','2026-02-21 16:16:32'),(26,1031,3,'按时签到奖励','2026-02-21 16:24:13'),(27,1031,1,'正常签退','2026-02-21 16:24:17'),(28,1031,3,'按时签到奖励','2026-02-21 16:30:57'),(29,1031,1,'正常签退','2026-02-21 17:03:52'),(30,1031,3,'按时签到奖励','2026-02-21 17:17:32'),(31,1031,1,'正常签退','2026-02-21 17:29:30'),(32,1031,1,'按时签到奖励','2026-02-21 17:30:01');
/*!40000 ALTER TABLE `credit_log` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-21 20:30:46
