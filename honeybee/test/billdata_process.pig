REGISTER '/home/xiangping/piglib/piggybank-0.15.0.jar'
DEFINE CSVExcelStorage org.apache.pig.piggybank.storage.CSVExcelStorage;
SET default_parallel 20;

customer  = LOAD '/users/xiangping/qianmi/datasource/customer_no.csv' USING CSVExcelStorage(',', 'NO_MULTILINE', 'UNIX', 'SKIP_INPUT_HEADER') AS (customer_no:chararray);
billdata  = LOAD '/users/xiangping/qianmi/datasource/billdata.csv' USING CSVExcelStorage(',', 'NO_MULTILINE', 'UNIX', 'SKIP_INPUT_HEADER') AS (customer_no:chararray,id_card:chararray,phone:chararray,user_name:chararray,call_pay:double,month:int);

filter_billdata = JOIN customer BY customer_no LEFT OUTER, billdata BY customer_no;
grp_billdata = GROUP filter_billdata BY billdata::customer_no;
billdata_agg = FOREACH grp_billdata {
				sorted = ORDER filter_billdata BY month DESC;
				l_6m = LIMIT sorted 6;
				l_3m = LIMIT sorted 3;
				l_1m = LIMIT sorted 1;
				GENERATE FLATTEN(l_1m.customer::customer_no), AVG(l_6m.call_pay), MAX(l_6m.call_pay), MIN(l_6m.call_pay), (MAX(l_6m.call_pay)-MIN(l_6m.call_pay)) ,AVG(l_3m.call_pay), AVG(l_1m.call_pay);
	}
STORE billdata_agg INTO '/users/xiangping/qianmi/result/billdatafilter/' USING PigStorage(',','-schema');

--msgdata  = LOAD '/users/xiangping/qianmi/datasource/msgdata.csv' USING CSVExcelStorage(',', 'NO_MULTILINE', 'UNIX', 'SKIP_INPUT_HEADER')
--			AS (customer_no:chararray,
--				id_card:chararray,
--				phone:chararray,
--				user_name:chararray,
--				trade_way:int,
--				receiver_phone:chararray,
--				send_time:chararray);
--
--filter_msgdata = JOIN customer BY customer_no LEFT OUTER, msgdata BY customer_no;
--grp_msgdata = GROUP filter_msgdata BY msgdata::customer_no;
--msgdata_agg = FOREACH grp_msgdata {
--				trade_way_one = FILTER filter_msgdata BY msgdata::trade_way==1;
--				trade_way_two = FILTER filter_msgdata BY msgdata::trade_way==2;
--				GENERATE FLATTEN(filter_msgdata.customer::customer_no) AS customer_no,
--						 FLATTEN(filter_msgdata.msgdata::phone) AS phone_number,
--						 COUNT(trade_way_one.trade_way) AS way_one_count,
--						 COUNT(trade_way_two.trade_way) AS way_two_count;
--	}
--STORE msgdata_agg INTO '/users/xiangping/qianmi/result/msgdatafilter/' USING PigStorage(',','-schema');
--

