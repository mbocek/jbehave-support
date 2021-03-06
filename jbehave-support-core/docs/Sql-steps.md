[Contents](../README.md)

## Sql steps

### Configuration
 
For each database used in the scenarios a bean of javax.sql.DataSource type with a database ID qualifier should be added to the Spring context with a qualifier that will be used in the scenario steps.  

```
@Bean
@Qualifier("CIF")
public DataSource testDatasource() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName(env.getProperty("db.driver"));
    dataSource.setUrl(env.getProperty("db.url"));
    dataSource.setUsername(env.getProperty("db.username"));
    dataSource.setPassword(env.getProperty("db.password"));
    return dataSource;
}
```

> SQL steps are not transactional. Each sql statement is executed in autocommit mode.


### Usage of Sql related steps:

The following example will query the *CIF* database with the provided query. 
The *:cuid* placeholder in the query will be replaced the *CUID* value from the test context as specified in the table below the query.  
`Given` or `When` can be used to call the step.
```
Given this query is performed on [CIF]:  
select cd.FIRST_NAME, cd.LAST_NAME, TO_CHAR(cd.DATE_OF_BIRTH, 'YYYY-MM-DD') as DATE_OF_BIRTH_DT, cd.TRUST_LEVEL
from client c
join client_detail cd on c.ID = cd.CLIENT_ID
where c.cuid = :cuid
order by cd.CREATION_DATE  
with parameters:  
| name | data      |  
| cuid | {CP:CUID} |
```

A step for querying a database without parameters is also available.  
`Given` or `When` can be used to call the step.


```
Given this query is performed on [LS]:
select first_name FROM (SELECT sr.first_name, COUNT(*) as cnt
FROM bsl_salesroom_representative sr
JOIN bsl_salesroom s ON sr.primary_salesroom = s.id
group by sr.first_name
having COUNT(*) >1)
WHERE ROWNUM=1

```

The next step will verify the query result matches the data in the provided example table. 
The first row contains column names. All the values will be injected from the test context.  

```
Then these rows match the query result:  
| FIRST_NAME        | LAST_NAME        | DATE_OF_BIRTH_DT     | TRUST_LEVEL        |  
| {CP:FIRST_NAME_1} | {CP:LAST_NAME_1} | {CP:DATE_OF_BIRTH_1} | {CP:TRUST_LEVEL_1} |  
| {CP:FIRST_NAME_2} | {CP:LAST_NAME_2} | {CP:DATE_OF_BIRTH_2} | {CP:TRUST_LEVEL_2} |  
```

For verifying only a subset of the query result set the following step should be used.

```
Then these rows are present in the query result:
| STRATEGY_NAME | WORKFLOW_CODE |
| prelim1       | DONE          |
```

If it's only required to check the number of rows returned the following step should be used.

```
Then the result set has 2 row(s) 
```

If it's necessary to save the query result into the test context so that they can be used later in the test scenario the following step does just that.  
`Given` or `When` can be used to call the step.

```
When these columns from the single-row query result are saved:
| name          | contextAlias     |
| CONTRACT_CODE | CONTRACT_NUMBER  |
| DT_CREATED    | DATE_CREATED     |
| SPRINT_CODE   | SPRINT_CODE      |
| NAME1         | FIRST_NAME       |
| NAME2         | LAST_NAME        |
| NAME3         | MIDDLE_NAME      |
| BAN           | BAN              |
| TA_ID         | TA_ID            |
```

The step above will only work with a result set with exactly one row. 
If more rows need to be saved the test context the following step should be used.
It will save all rows with an index number in [ ] such as FIRST_NAME[0].  

```
When these columns from the multi-row query result are saved:
| name           | contextAlias     |
| FIRST_NAME     | FIRST_NAME       |
| LAST_NAME      | LAST_NAME        |
| SPRINT_CODE    | SPRINT_CODE      |
| DT_ACTIVATION  | ACTIVATION_DATE  |
| CODE           | CODE             |
| IS_BLOCKED     | BLOCKED          |
| IS_DEACTIVATED | DEACTIVATED      |
```

It's also possible to run an UPDATE, INSERT or DELETE statements against the selected database. The following step does just that.
The contextAlias column in the parameters is optional. 
If used and a name of a new context variable is entered into that column the value from the value column is stored in the test context under that variable name.  
`Given` or `When` can be used to call the step.
```
Given this update is performed on [LS]:
    update bsl_sprint_credit_data
      set creation_date = sysdate - (:fcd + 1)
      where id = :id
with parameters:
|name   |data                         | contextAlias |
|id     |{CP:SPRINT_CREDIT_DATA_ID}   |              | 
|fcd    |{CP:FICO_COPY_DAYS}          |              |
```

The step below shows how to execute a DELETE statement with no parameters
```
When this update is performed on [TEST]:
delete from person
```

---
