### Create maven run configuration:

In IntelliJ,

1. Click run -> Edit configuration
2. Create/add configuration
3. Name the configuration based on the context and create the below goal in the command line field
   ```clean install site -DsuiteFile=api_regression.xml -DtestDataConfig=sampleData -DenvConfig=test```
4. Click apply and save it