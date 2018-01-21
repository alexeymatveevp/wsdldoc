# wsdldoc
The tool can be used to generate HTML documentation out of WSDL file

Currently only loading from URL is supported (tested) for example

`http://tmv716.devlab.de.tmo:7501/tibet/services/soabp/eShopServices?wsdl`

...should return valid WSDL in order to parse it properly.

The order of services in generated doc will be the same as the order of `-s` input parameters

The tool can be assembled using `gradlew assemble` - it will be an executable jar under `build/libs`

Try launching `java -jar wsdldoc-1.0-SNAPSHOT.jar` without parameters or with `-h` option and see the help screen

```
usage: wsdldoc
 -d,--destination <arg>   the destination folder of documentation
                          (absolute or relative), it will generate
                          index.html and statics in this folder (there
                          should be write access to the folder)
 -f,--filename <arg>      the destination file name (default is
                          index.html)
 -h,--help                shows this help output
 -s,--source <arg>        one or multiple URLs with source WSDLs location;
                          the schemas in WSDL's should have schemaLocation
                          in order to correctly generate all the types
 -t,--title <arg>         the title of the documentation, like "eCompany"
                          (WSDL by default)
```

## Screenshots

On top of each documentation there is a title and service operations list.

<img src="https://github.com/davidluckystar/wsdldoc/blob/master/screenshots/1.png" width="100%" style="display: inline-block">

All found types are defined in separate blocks. Each type has a link.

<img src="https://github.com/davidluckystar/wsdldoc/blob/master/screenshots/2.png" width="100%" style="display: inline-block">

## Roadmap
* Add support for "choice" XSD tag
* Better support for extensions

## Usage examples


java -jar wsdldoc-1.0-SNAPSHOT.jar -t=eCompany -s=http://tmv716.devlab.de.tmo:7501/ecompany/services/bulkExport?wsdl -s=http://tmv716.devlab.de.tmo:7501/ecompany/services/fulfillment?wsdl -s=http://tmv716.devlab.de.tmo:7501/ecompany/services/getContextData?wsdl -s=http://tmv716.devlab.de.tmo:7501/ecompany/services/onlineRead?wsdl -s=http://tmv716.devlab.de.tmo:7501/ecompany/services/getTariffRights?wsdl -d=~/wsdldoc


java -jar wsdldoc-1.0-SNAPSHOT.jar -t=B2B -s=http://tmv716.devlab.de.tmo:7501/tibet/b2b/services/contract_vcs17_00?wsdl -s=http://tmv716.devlab.de.tmo:7501/tibet/b2b/services/customer_vcs17_00?wsdl -s=http://tmv716.devlab.de.tmo:7501/tibet/b2b/services/fulfillment_vcs17_00?wsdl -s=http://tmv716.devlab.de.tmo:7501/tibet/b2b/services/ordertracking_vcs17_00?wsdl -s=http://tmv716.devlab.de.tmo:7501/tibet/b2b/services/product_vcs17_00?wsdl -d=~/wsdldoc


java -jar wsdldoc-1.0-SNAPSHOT.jar -t=eShop -s=http://tmv716.devlab.de.tmo:7501/tibet/services/eShopServices?wsdl -d=~/wsdldoc

