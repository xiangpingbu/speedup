register 'parse_qianmi_file_pig.py' using jython as pyudf;
register '/home/hadoop/pig-0.16.0/lib/jython-standalone-2.7.0.jar';
qianmi_file = LOAD '/user/lifeng/test/qianmi/log_mobile_service.sql' USING PigStorage('$') as (line:chararray);

--match_result = FOREACH qianmi_file  GENERATE INDEXOF (line,'INSERT',0);
l = limit qianmi_file 100;

X = FILTER l BY (line matches '.*INSERT.*');



user_data  = foreach X generate pyudf.load(line)

dump user_data;

