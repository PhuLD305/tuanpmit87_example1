/*
Navicat MySQL Data Transfer

Source Server         : Local
Source Server Version : 50716
Source Host           : localhost:3306
Source Database       : db_springboot

Target Server Type    : MYSQL
Target Server Version : 50716
File Encoding         : 65001

Date: 2019-04-09 11:27:04
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `tbl_company`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_company`;
CREATE TABLE `tbl_company` (
  `company_internal_id` int(10) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(50) NOT NULL,
  `address` varchar(100) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `telephone` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`company_internal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbl_company
-- ----------------------------
INSERT INTO `tbl_company` VALUES ('1', 'Luvina JSC', '106 Hoang Quoc Viet', 'company@luvina.net', '0240123456');
INSERT INTO `tbl_company` VALUES ('2', 'FPT Software', '111 Duy Tan', 'company@fptsoft.com', '0215424598');
INSERT INTO `tbl_company` VALUES ('21', 'Công ty CPPM ABC', '123 abc', 'abc@gmail.com', '0145214578');
INSERT INTO `tbl_company` VALUES ('22', 'Công ty COP', 'aaaa', 'aaa@gmail.com', '1245145789');
INSERT INTO `tbl_company` VALUES ('23', 'Công ty TNHH Hoa Thiên Cốt', '111 abc', 'hoathiencot@gmail.com', '0240123456');
INSERT INTO `tbl_company` VALUES ('24', 'Cong Ty Co Phan Pm Luvina', '106 hoàng quốc việt', 'abc@gmail.com', '0240123456');

-- ----------------------------
-- Table structure for `tbl_insurance`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_insurance`;
CREATE TABLE `tbl_insurance` (
  `insurance_internal_id` int(10) NOT NULL AUTO_INCREMENT,
  `insurance_number` varchar(10) NOT NULL,
  `insurance_start_date` date NOT NULL,
  `insurance_end_date` date NOT NULL,
  `place_of_register` varchar(50) NOT NULL,
  PRIMARY KEY (`insurance_internal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbl_insurance
-- ----------------------------
INSERT INTO `tbl_insurance` VALUES ('1', '1121759210', '2019-01-01', '2019-12-31', 'BV Thể Thao');
INSERT INTO `tbl_insurance` VALUES ('2', '1121759211', '2018-01-01', '2019-12-31', 'BV Giao thong van tai');
INSERT INTO `tbl_insurance` VALUES ('3', '1121759212', '2018-02-01', '2019-12-31', 'BVDK An Khanh');
INSERT INTO `tbl_insurance` VALUES ('4', '1121759213', '2018-01-01', '2018-12-31', 'BV Quoc te Hong Ngoc');
INSERT INTO `tbl_insurance` VALUES ('5', '1121759214', '2018-01-01', '2019-12-31', 'Benh vien E1 update');
INSERT INTO `tbl_insurance` VALUES ('19', '1121759215', '2019-01-01', '2019-12-31', 'BV Đại học Y Hà Nội');
INSERT INTO `tbl_insurance` VALUES ('20', '1121759216', '2019-04-01', '2019-12-31', 'BV Cầu Giấy');
INSERT INTO `tbl_insurance` VALUES ('21', '1121759217', '2019-04-01', '2019-12-31', 'BVDK Phú Xuyên');
INSERT INTO `tbl_insurance` VALUES ('24', '1121759218', '2018-04-01', '2019-03-31', 'BV Y hoc co truyen');
INSERT INTO `tbl_insurance` VALUES ('28', '1121759219', '2019-01-01', '2020-12-31', 'BV Quoc te Thu Cuc');
INSERT INTO `tbl_insurance` VALUES ('36', '1121759222', '2019-04-04', '2019-04-30', 'BV a1');
INSERT INTO `tbl_insurance` VALUES ('38', '1121759221', '2019-04-04', '2020-04-01', 'BV a3');
INSERT INTO `tbl_insurance` VALUES ('39', '1121759223', '2019-04-04', '2020-04-01', 'BV a4');
INSERT INTO `tbl_insurance` VALUES ('40', '1121759224', '2019-04-04', '2020-04-01', 'BV a5');
INSERT INTO `tbl_insurance` VALUES ('41', '1121759225', '2019-04-04', '2020-04-01', 'BV E1');
INSERT INTO `tbl_insurance` VALUES ('43', '1121759226', '2019-04-01', '2019-04-30', 'BV D1');
INSERT INTO `tbl_insurance` VALUES ('44', '1121759228', '2019-01-01', '2019-12-31', 'BV abc1');
INSERT INTO `tbl_insurance` VALUES ('45', '1121759229', '2018-01-01', '2019-12-31', 'BV Bắc Giang');
INSERT INTO `tbl_insurance` VALUES ('47', '1121759220', '2018-04-01', '2019-04-01', 'BV Viet Duc');
INSERT INTO `tbl_insurance` VALUES ('48', '1121759227', '2019-01-01', '2019-12-31', 'BV Bắc Giang 2');
INSERT INTO `tbl_insurance` VALUES ('49', '1121759230', '2019-04-01', '2020-03-31', 'BV Bạch Mai');
INSERT INTO `tbl_insurance` VALUES ('50', '1121759231', '2019-04-01', '2020-04-01', 'BV Thu Cúc');
INSERT INTO `tbl_insurance` VALUES ('52', '1111111111', '2019-04-01', '2020-04-01', 'BV thanh nhàn');

-- ----------------------------
-- Table structure for `tbl_user`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_user`;
CREATE TABLE `tbl_user` (
  `user_internal_id` int(10) NOT NULL AUTO_INCREMENT,
  `company_internal_id` int(10) NOT NULL,
  `insurance_internal_id` int(10) NOT NULL,
  `username` varchar(15) NOT NULL,
  `password` varchar(32) NOT NULL,
  `user_full_name` varchar(50) NOT NULL,
  `user_sex_division` char(2) NOT NULL,
  `birthdate` date DEFAULT NULL,
  PRIMARY KEY (`user_internal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbl_user
-- ----------------------------
INSERT INTO `tbl_user` VALUES ('1', '1', '1', 'admin', '202cb962ac59075b964b07152d234b70', 'Admin', '01', '1987-05-26');
INSERT INTO `tbl_user` VALUES ('2', '2', '2', 'thaihv', '202cb962ac59075b964b07152d234b70', 'Hoang Van Thai', '01', '1989-04-02');
INSERT INTO `tbl_user` VALUES ('3', '1', '3', 'tuongqt', '202cb962ac59075b964b07152d234b70', 'Quach Thu Tuong', '02', '1986-07-02');
INSERT INTO `tbl_user` VALUES ('4', '1', '4', 'quadv', '202cb962ac59075b964b07152d234b70', 'Duong Van Qua', '01', '1979-01-02');
INSERT INTO `tbl_user` VALUES ('5', '22', '5', 'dunght', '202cb962ac59075b964b07152d234b70', 'Hoang Thuy Dung', '02', '1991-11-12');
INSERT INTO `tbl_user` VALUES ('9', '1', '19', 'tinhca', '202cb962ac59075b964b07152d234b70', 'Quach Ba Tinh', '01', '1989-11-20');
INSERT INTO `tbl_user` VALUES ('10', '2', '20', 'tahl', '202cb962ac59075b964b07152d234b70', 'Hoang Lao Ta', '01', '1969-05-06');
INSERT INTO `tbl_user` VALUES ('11', '1', '21', 'anhtt', '202cb962ac59075b964b07152d234b70', 'Trinh Thi Anh', '02', '1989-04-02');
INSERT INTO `tbl_user` VALUES ('13', '21', '24', 'manta', '202cb962ac59075b964b07152d234b70', 'Trieu A Man', '02', '1989-07-09');
INSERT INTO `tbl_user` VALUES ('14', '1', '28', 'kytv', '202cb962ac59075b964b07152d234b70', 'Truong Vo Ky', '01', '1987-10-01');
INSERT INTO `tbl_user` VALUES ('22', '21', '36', 'xichct', '202cb962ac59075b964b07152d234b70', 'Cau Thien Xich', '02', '1979-04-22');
INSERT INTO `tbl_user` VALUES ('24', '23', '38', 'nhanct', '202cb962ac59075b964b07152d234b70', 'Cau Thien Nhan', '01', '1992-04-02');
INSERT INTO `tbl_user` VALUES ('25', '22', '39', 'phuqt', '202cb962ac59075b964b07152d234b70', 'Quach Thi Phu', '02', '1989-04-12');
INSERT INTO `tbl_user` VALUES ('26', '2', '40', 'cuoclh', '202cb962ac59075b964b07152d234b70', 'Lo Huu Cuoc', '01', '1989-04-02');
INSERT INTO `tbl_user` VALUES ('27', '1', '41', 'duongtieu', '202cb962ac59075b964b07152d234b70', 'Duong Tieu', '01', '1969-09-02');
INSERT INTO `tbl_user` VALUES ('29', '2', '43', 'phamdao', '202cb962ac59075b964b07152d234b70', 'Pham Dao', '01', '1979-04-23');
INSERT INTO `tbl_user` VALUES ('30', '2', '44', 'luyenlv', '202cb962ac59075b964b07152d234b70', 'Le Van Luyen', '01', '1989-04-02');
INSERT INTO `tbl_user` VALUES ('31', '2', '45', 'thanhqp', '202cb962ac59075b964b07152d234b70', 'Quach Phu Thanh', '01', '1976-04-12');
INSERT INTO `tbl_user` VALUES ('33', '1', '47', 'nhuoccc', '202cb962ac59075b964b07152d234b70', 'Chu Chi Nhuoc', '02', '1957-09-07');
INSERT INTO `tbl_user` VALUES ('34', '2', '48', 'saulm', '3389dae361af79b04c9c8e7057f60cc6', 'Ly Mac Sau', '02', '1989-10-02');
INSERT INTO `tbl_user` VALUES ('35', '1', '49', 'hungtv', '202cb962ac59075b964b07152d234b70', 'Tran Viet Hung', '01', '1985-10-09');
INSERT INTO `tbl_user` VALUES ('36', '1', '50', 'haipd', '202cb962ac59075b964b07152d234b70', 'Pham Duy Hai', '01', '1991-02-06');
INSERT INTO `tbl_user` VALUES ('38', '24', '52', 'abcd', '202cb962ac59075b964b07152d234b70', 'Test Anh Yeu Em', '01', '1999-04-09');
