-- phpMyAdmin SQL Dump
-- version 4.8.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 28, 2018 at 09:10 PM
-- Server version: 10.1.34-MariaDB
-- PHP Version: 7.2.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `voiceapp`
--
DROP DATABASE `voiceapp`;
CREATE DATABASE `voiceapp`;
USE `voiceapp`;

-- --------------------------------------------------------

--
-- Table structure for table `intents`
--

CREATE TABLE `intents` (
  `tag` varchar(50) NOT NULL,
  `pattern` varchar(1000) NOT NULL,
  `response` varchar(200) NOT NULL,
  `actionable` tinyint(1) NOT NULL,
  `same_intent_message` varchar(200) NOT NULL,
  `action_stack` varchar(200) NOT NULL,
  `context_builder` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `intents`
--

INSERT INTO `intents` (`tag`, `pattern`, `response`, `actionable`, `same_intent_message`, `action_stack`, `context_builder`) VALUES
('CallerIntro', 'Hello my name is\r\nHello I am calling from', 'Yes, how can I help you?\r\nYes, tell me', 0, 'Yes how can I help you', '', ''),
('CollectOrderNumber', 'Order number is dnumber\r\nMy order number is dnumber', '', 0, '', '', 'AddOrReplace OrderNumber #NUMBER'),
('Greet', 'Hello\r\nHi', '', 0, 'Yes, how can I help you?', '', ''),
('No', 'No\r\nDo not do that\r\nI did not mean that\r\nWrong', '', 0, '', '', ''),
('Noise', 'You hear me\r\nAm I audible', 'Yes I can hear you', 0, '', '', ''),
('OrderCancel', 'Cancel my order\r\nCancel the order\r\nDelete an order\r\nRemove order', 'What is your order number\r\nMay I know the order number', 0, '', '', ''),
('OrderCancelWithNumber', 'Cancel my order dnumber\r\nCancel order dnumber', 'Please confirm that you want to cancel #NUMBER', 1, '', '', 'AddOrReplace OrderNumber #NUMBER'),
('OrderStatus', 'Check order status\r\nCheck order\r\nKnow the order status\r\nWhat is the status of my order\r\nCheck the status of an order', 'What is your order number\r\nMay I know the order number', 0, '', '', ''),
('OrderStatusWithNumber', 'What is the status of my order dnumber\r\nCheck the status of order dnumber\r\nknow the status of order dnumber\r\nCheck my order dnumber', '', 1, '', 'PERFORM(GET_ORDER_STATUS(#NUMBER))', 'AddOrReplace OrderNumber #NUMBER'),
('Thanks', 'Thanks\r\nThank you\r\nThat was helpful', 'Happy to help\r\nAny time\r\nMy pleasure', 0, '', '', ''),
('Yes', 'Yes\r\nI confirm\r\nPlease procced\r\nGo Ahead', '', 0, '', '', '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `intents`
--
ALTER TABLE `intents`
  ADD PRIMARY KEY (`tag`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
