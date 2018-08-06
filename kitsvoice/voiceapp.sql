-- phpMyAdmin SQL Dump
-- version 4.8.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 06, 2018 at 10:25 AM
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
drop database `voiceapp`
create database `voiceapp`
use `voiceapp`
-- --------------------------------------------------------

--
-- Table structure for table `intentexecution`
--

CREATE TABLE `intentexecution` (
  `Intent` varchar(50) NOT NULL,
  `Step` int(11) NOT NULL,
  `Action` varchar(50) NOT NULL,
  `Parameter` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `intentexecution`
--

INSERT INTO `intentexecution` (`Intent`, `Step`, `Action`, `Parameter`) VALUES
('CallerIntro', 1, 'PLAY_RANDOM', 'Yes, how can I help you?:Hi, how may I help you?'),
('CollectOrderNumber', 1, 'EVALUATE_VARIABLE', 'OrderNumber'),
('No', 1, 'POP_LAST_INTENT', 'NO_PARAM'),
('OrderCancel', 1, 'VARIABLE_REQUIRED', 'OrderNumber|Please tell me you order number:What is your order number?:Can you please tell me your order number?:May I know the order number please?'),
('OrderCancel', 2, 'PLAY_RANDOM', 'Are you sure you want to cancel the order [OrderNumber]?'),
('OrderCancel', 3, 'EXECUTE', 'OrderCancel/[OrderNumber]'),
('OrderCancel', 4, 'PLAY_RANDOM', 'Your order has been cancelled:We have cancelled your order:Order has been cancelled successfully'),
('OrderStatus', 1, 'VARIABLE_REQUIRED', 'OrderNumber|Please tell me you order number:What is your order number?:Can you please tell me your order number?:May I know the order number please?'),
('OrderStatus', 2, 'EXECUTE', '/orderStatus/[OrderNumber]'),
('OrderStatus', 3, 'PLAY_RANDOM', 'Status of your order is [orderState]'),
('Yes', 1, 'POP_LAST_ACTION', 'NO_PARAM');

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
('CallerIntro', 'Hello my name is\r\nHello I am calling from\r\nHello this is\r\nHi this is', 'Yes, how can I help you?\r\nYes, tell me', 0, 'Yes how can I help you', '', ''),
('CollectOrderNumber', 'Order number is dnumber\r\nMy order number is dnumber', '', 0, '', '', 'AddOrReplace OrderNumber #NUMBER'),
('Greet', 'Hello\r\nHi', '', 0, 'Yes, how can I help you?', '', ''),
('No', 'No\r\nDo not do that\r\nI did not mean that\r\nWrong', '', 0, '', '', ''),
('Noise', 'You hear me\r\nAm I audible', 'Yes I can hear you', 0, '', '', ''),
('OrderCancel', 'Is it possible to cancel delete remove order\r\nMay I delete cancel delete remove order\r\nCan not you cancel delete remove order\r\nCancel Remove Delete the order from my profile\r\nPlease cancel the order\r\nMay I cancel a order please\r\nDelete the order from my profile', 'What is your order number\r\nMay I know the order number', 0, '', '', ''),
('OrderCancelWithNumber', 'Is it possible to cancel delete remove order dnumber\r\nMay I delete cancel delete remove order dnumber\r\nCan not you cancel delete remove order dnumber\r\nCancel Remove Delete the order from my profile dnumber\r\nremove the order id with dnumber\r\nHi remove the order with dnumber\r\ncan not you cancel the order dnumber\r\ncan not you cancel the order with dnumber\r\nHelp me out to cancel a order with dnumber', 'Please confirm that you want to cancel #NUMBER', 1, '', '', 'AddOrReplace OrderNumber #NUMBER'),
('OrderStatus', 'you check order status\r\nknow the status of order\r\nconfirm the order status\r\ncurrent status of order\r\nPlease let me know the order status\r\nCould you please help me to get the current status of the order', 'What is your order number\r\nMay I know the order number\r\nTell me the order status', 0, '', '', ''),
('OrderStatusWithNumber', 'Please confirm the order status against dnumber\r\ncould you please tell me the order status for dnumber\r\nNeed to know the status of the order dnumber', '', 1, '', 'PERFORM(GET_ORDER_STATUS(#NUMBER))', 'AddOrReplace OrderNumber #NUMBER'),
('Thanks', 'Thanks\r\nThank you\r\nThat was helpful', 'Happy to help\r\nAny time\r\nMy pleasure\r\nYou\'re welcome', 0, '', '', ''),
('Yes', 'Yes\r\nI confirm\r\nProcced\r\nGo Ahead', '', 0, '', '', '');

-- --------------------------------------------------------

--
-- Table structure for table `response`
--

CREATE TABLE `response` (
  `id` int(11) NOT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `scene` varchar(20) NOT NULL,
  `response` varchar(200) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `response`
--

INSERT INTO `response` (`id`, `timestamp`, `scene`, `response`) VALUES
(38, '2018-08-05 17:55:19', 'q1', 'ok');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `intentexecution`
--
ALTER TABLE `intentexecution`
  ADD PRIMARY KEY (`Intent`,`Step`);

--
-- Indexes for table `intents`
--
ALTER TABLE `intents`
  ADD PRIMARY KEY (`tag`);

--
-- Indexes for table `response`
--
ALTER TABLE `response`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `response`
--
ALTER TABLE `response`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
