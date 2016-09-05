# wsdldoc
The tool can be used to generate HTML documentation out of WSDL file

Currently only loading from URL is supported (tested)
``

The order of services in generated doc will be the same as the order of `-s` input parameters

## Examples
### eCompany
`java -jar wsdldoc-1.0-SNAPSHOT.jar -t=eCompany -s=http://tmv716.devlab.de.tmo:7501/ecompany/services/bulkExport?wsdl -s=http://tmv716.devlab.de.tmo:7501/ecompany/services/fulfillment?wsdl -s=http://tmv716.devlab.de.tmo:7501/ecompany/services/getContextData?wsdl -s=http://tmv716.devlab.de.tmo:7501/ecompany/services/onlineRead?wsdl -s=http://tmv716.devlab.de.tmo:7501/ecompany/services/getTariffRights?wsdl -d=~/wsdldoc`
### B2B
`java -jar wsdldoc-1.0-SNAPSHOT.jar -t=B2B -s=http://tmv716.devlab.de.tmo:7501/tibet/b2b/services/contract_vcs17_00?wsdl -s=http://tmv716.devlab.de.tmo:7501/tibet/b2b/services/customer_vcs17_00?wsdl -s=http://tmv716.devlab.de.tmo:7501/tibet/b2b/services/fulfillment_vcs17_00?wsdl -s=http://tmv716.devlab.de.tmo:7501/tibet/b2b/services/ordertracking_vcs17_00?wsdl -s=http://tmv716.devlab.de.tmo:7501/tibet/b2b/services/product_vcs17_00?wsdl -d=~/wsdldoc`