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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_zh_0900_as_cs NOT NULL COMMENT '学号',
  `password` varchar(255) COLLATE utf8mb4_zh_0900_as_cs NOT NULL,
  `real_name` varchar(50) COLLATE utf8mb4_zh_0900_as_cs NOT NULL,
  `college` varchar(64) COLLATE utf8mb4_zh_0900_as_cs DEFAULT NULL COMMENT '所属学院',
  `credit_score` int DEFAULT '100' COMMENT '信用分',
  `avatar` varchar(255) COLLATE utf8mb4_zh_0900_as_cs DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `major` varchar(64) COLLATE utf8mb4_zh_0900_as_cs DEFAULT NULL COMMENT '专业',
  `last_login_time` datetime DEFAULT NULL COMMENT '上次登录/入馆时间',
  `phone` varchar(20) COLLATE utf8mb4_zh_0900_as_cs DEFAULT NULL COMMENT '手机号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1035 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_zh_0900_as_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'221603020401','e10adc3949ba59abbe56e057f20f883e','abc','计算机与信息安全学院',81,'/images/e979b4b4-0a74-4c96-83ce-e5b83bf4e229.png','2026-02-01 16:04:52','网络工程','2026-02-21 21:57:07',NULL),(2,'221603020417','e10adc3949ba59abbe56e057f20f883e','aaa',NULL,95,'https://api.dicebear.com/7.x/avataaars/svg?seed=221603020417','2026-02-01 16:06:09',NULL,'2026-02-19 15:16:41',NULL),(1001,'220000000001','e10adc3949ba59abbe56e057f20f883e','张伟',NULL,100,NULL,'2026-02-01 16:43:24',NULL,NULL,NULL),(1002,'220000000002','e10adc3949ba59abbe56e057f20f883e','王芳',NULL,98,NULL,'2026-02-01 16:43:24',NULL,NULL,NULL),(1003,'220000000003','e10adc3949ba59abbe56e057f20f883e','李娜',NULL,100,NULL,'2026-02-01 16:43:24',NULL,NULL,NULL),(1004,'220000000004','e10adc3949ba59abbe56e057f20f883e','刘洋',NULL,95,NULL,'2026-02-01 16:43:24',NULL,NULL,NULL),(1005,'220000000005','e10adc3949ba59abbe56e057f20f883e','陈杰',NULL,80,NULL,'2026-02-01 16:43:24',NULL,NULL,NULL),(1006,'220000000006','e10adc3949ba59abbe56e057f20f883e','杨敏',NULL,100,NULL,'2026-02-01 16:43:24',NULL,NULL,NULL),(1007,'220000000007','e10adc3949ba59abbe56e057f20f883e','赵强',NULL,100,NULL,'2026-02-01 16:43:24',NULL,NULL,NULL),(1008,'220000000008','e10adc3949ba59abbe56e057f20f883e','周婷',NULL,92,NULL,'2026-02-01 16:43:24',NULL,NULL,NULL),(1009,'220000000009','e10adc3949ba59abbe56e057f20f883e','吴磊',NULL,100,NULL,'2026-02-01 16:43:24',NULL,NULL,NULL),(1010,'220000000010','e10adc3949ba59abbe56e057f20f883e','徐静',NULL,100,NULL,'2026-02-01 16:43:24',NULL,NULL,NULL),(1011,'230000000001','e10adc3949ba59abbe56e057f20f883e','赵云飞',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhao','2026-02-04 18:31:31',NULL,NULL,NULL),(1012,'230000000002','e10adc3949ba59abbe56e057f20f883e','钱小二',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Qian','2026-02-04 18:31:31',NULL,NULL,NULL),(1013,'230000000003','e10adc3949ba59abbe56e057f20f883e','孙悟空',NULL,95,'https://api.dicebear.com/7.x/avataaars/svg?seed=Sun','2026-02-04 18:31:31',NULL,NULL,NULL),(1014,'230000000004','e10adc3949ba59abbe56e057f20f883e','李白',NULL,95,'https://api.dicebear.com/7.x/avataaars/svg?seed=Li','2026-02-04 18:31:31',NULL,NULL,NULL),(1015,'230000000005','e10adc3949ba59abbe56e057f20f883e','周瑜',NULL,98,'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhou','2026-02-04 18:31:31',NULL,NULL,NULL),(1016,'230000000006','e10adc3949ba59abbe56e057f20f883e','吴用',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Wu','2026-02-04 18:31:31',NULL,NULL,NULL),(1017,'230000000007','e10adc3949ba59abbe56e057f20f883e','郑成功',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Zheng','2026-02-04 18:31:31',NULL,NULL,NULL),(1018,'230000000008','e10adc3949ba59abbe56e057f20f883e','王昭君',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Wang','2026-02-04 18:31:31',NULL,NULL,NULL),(1019,'230000000009','e10adc3949ba59abbe56e057f20f883e','冯宝宝',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Feng','2026-02-04 18:31:31',NULL,NULL,NULL),(1020,'230000000010','e10adc3949ba59abbe56e057f20f883e','陈圆圆',NULL,99,'https://api.dicebear.com/7.x/avataaars/svg?seed=Chen','2026-02-04 18:31:31',NULL,NULL,NULL),(1021,'230000000011','e10adc3949ba59abbe56e057f20f883e','褚遂良',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Chu','2026-02-04 18:31:31',NULL,NULL,NULL),(1022,'230000000012','e10adc3949ba59abbe56e057f20f883e','卫子夫',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Wei','2026-02-04 18:31:31',NULL,NULL,NULL),(1023,'230000000013','e10adc3949ba59abbe56e057f20f883e','蒋干',NULL,80,'https://api.dicebear.com/7.x/avataaars/svg?seed=Jiang','2026-02-04 18:31:31',NULL,NULL,NULL),(1024,'230000000014','e10adc3949ba59abbe56e057f20f883e','沈万三',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Shen','2026-02-04 18:31:31',NULL,NULL,NULL),(1025,'230000000015','e10adc3949ba59abbe56e057f20f883e','韩信',NULL,90,'https://api.dicebear.com/7.x/avataaars/svg?seed=Han','2026-02-04 18:31:31',NULL,NULL,NULL),(1026,'230000000016','e10adc3949ba59abbe56e057f20f883e','杨玉环',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Yang','2026-02-04 18:31:31',NULL,NULL,NULL),(1027,'230000000017','e10adc3949ba59abbe56e057f20f883e','朱元璋',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhu','2026-02-04 18:31:31',NULL,NULL,NULL),(1028,'230000000018','e10adc3949ba59abbe56e057f20f883e','秦始皇',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Qin','2026-02-04 18:31:31',NULL,NULL,NULL),(1029,'230000000019','e10adc3949ba59abbe56e057f20f883e','尤二姐',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=You','2026-02-04 18:31:31',NULL,NULL,NULL),(1030,'230000000020','e10adc3949ba59abbe56e057f20f883e','许仙',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=Xu','2026-02-04 18:31:31',NULL,NULL,NULL),(1031,'admin','e10adc3949ba59abbe56e057f20f883e','超级管理员',NULL,100,'/images/3f26a067-f49c-4ef9-bdcf-6fcb85a89b74.jpg','2026-02-11 14:50:40','图书馆管理员','2026-02-22 11:54:21','18277455555'),(1032,'221603020402','e10adc3949ba59abbe56e057f20f883e','opp',NULL,100,'https://api.dicebear.com/7.x/avataaars/svg?seed=221603020402','2026-02-20 13:24:43',NULL,'2026-02-20 13:24:49',NULL),(1033,'221603020404','e10adc3949ba59abbe56e057f20f883e','啊水水',NULL,100,'https://api.dicebear.com/7.x/lorelei/svg?seed=Sasha&backgroundColor=b6e3f4','2026-02-20 13:27:58',NULL,'2026-02-20 13:28:05',NULL),(1034,'221603020405','e10adc3949ba59abbe56e057f20f883e','三角洲',NULL,100,'https://api.dicebear.com/7.x/adventurer/svg?seed=Abby&backgroundColor=d1d4f9','2026-02-21 20:43:34',NULL,NULL,'15148645951');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-22 11:55:56
