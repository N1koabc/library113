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
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '简要',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图URL',
  `content` text COMMENT '详细内容(可选)',
  `publish_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `like_count` int DEFAULT '0',
  `dislike_count` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` VALUES (1,'哈哈哈','','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg',NULL,'2026-02-15 17:19:28',0,0),(2,'十大','','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg',NULL,'2026-02-15 17:19:38',0,0),(3,'撒大大实打实','','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg',NULL,'2026-02-15 17:20:01',0,0),(6,'哈基米南北','ajdhuawhduawh','/images/76e285fe-e2c4-4641-9247-7583451d8768.png','哇啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊哇哇哇哇哇哇哇<br><img src=\"/images/779f15b0-be95-4134-a3f0-cc73f41aabc7.png\" style=\"max-width:100%; border-radius: 8px; margin: 10px 0;\"><br>','2026-02-15 18:12:44',1,0),(7,'撒大苏打实打实的','','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg','','2026-02-16 16:28:25',0,0),(8,'撒大大大苏打撒旦撒旦撒到达','','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg','','2026-02-16 16:28:29',0,0),(9,'十大啊啊啊啊啊啊啊啊啊啊啊啊','大撒大撒','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg','大撒大撒大撒大苏打','2026-02-16 16:28:46',0,0),(10,'我打打打','','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg','','2026-02-16 16:28:52',0,0),(12,'啊啊啊啊我','','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg','','2026-02-16 16:28:56',0,0),(13,'萨达萨达是','','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg','撒大大','2026-02-16 16:29:12',0,0),(14,'哇额外企鹅去','','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg','','2026-02-16 16:29:14',0,0),(15,'大撒大撒','','https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg','','2026-02-16 16:29:16',0,0),(16,'咕咕嘎嘎','','/images/5032ab67-c762-413d-a33a-d958877a2ef2.jpg','','2026-02-16 16:56:11',3,0);
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
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
