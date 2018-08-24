create table caller_ID(caller_number varchar(50), caller_sid varchar(50), call_time timestamp);
create table call_log(id bigint auto_increment, caller_Sid varchar(50), direction varchar(3), transcript varchar(500), intent varchar(50), call_time timestamp);
create table test_intent(id bigint auto_increment,sentence varchar(500),expected_intent varchar(50));
create table action_element(id bigint auto_increment,caller_Sid varchar(50),intent varchar(50),action varchar(500),parameter varchar(500), expected_intent varchar(500));
create table memory_stick(id bigint auto_increment,caller_Sid varchar(50),variable varchar(50),type varchar(50),value varchar(500));
