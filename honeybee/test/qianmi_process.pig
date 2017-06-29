qianmi_file = LOAD 'user/lifeng/test/qianmi/log_mobile_service.sql' USING PigStorage('$') as (line:chararray);

--match_result = FOREACH qianmi_file  GENERATE INDEXOF (line,'INSERT',0);

X = FILTER qianmi_file BY (line matches '.*INSERT.*');

l = limit X 1;

