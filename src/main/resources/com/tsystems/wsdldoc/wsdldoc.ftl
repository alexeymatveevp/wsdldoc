<html>

<head>
    <link rel="stylesheet" type="text/css" href="bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="wsdl_style.css" />
</head>

<body>
<div class="jumbotron">
    <div class="container">
        <h1>${title} documentation</h1>
    </div>
</div>

<div class="page-header">
    <h1>Services</h1>
</div>
<ul class="wsdl-services">
<#list services as service>
    <li>
        <h2>${service.name}</h2>
        <h3>Methods</h3>
        <ul>
            <#list service.methods as method>
                <li class="container-fluid">
                    <div class="row">
                        <div class="col-lg-3">
                            <h4>${method.name}</h4>
                        </div>
                        <div class="col-lg-9">
                            <div>
                                <strong>Request: ${method.request.name}</strong>
                                <#if method.request.description ??>
                                    <span>Description:</span>
                                    <p>${method.request.description}</p>
                                </#if>
                                <#if method.request.sequence??>
                                    <ul>
                                        <#list method.request.sequence as type>
                                            <li>
                                                <#if type.nativeType>
                                                    <span>${type.name} : <#if type.typeName??>${type.typeName}</#if></span>
                                                <#else>
                                                    <span>${type.name} : <#if type.typeName??><a href="#${type.typeName}">${type.typeName}</a></#if></span>
                                                </#if>
                                            </li>
                                        </#list>
                                    </ul>
                                </#if>
                            </div>

                            <div>
                                <strong>Response: ${method.response.name}</strong>
                                <#if method.response.description ??>
                                    <span>Description:</span>
                                    <p>${method.response.description}</p>
                                </#if>
                                <#if method.response.sequence??>
                                    <ul>
                                    <#--<span>response parameters:</span>-->
                                        <#list method.response.sequence as type>
                                            <li>
                                                <#if type.nativeType>
                                                    <span>${type.name} : <#if type.typeName??>${type.typeName}</#if></span>
                                                <#else>
                                                    <span>${type.name} : <#if type.typeName??><a href="#${type.typeName}">${type.typeName}</a></#if></span>
                                                </#if>
                                            <#--<span>min: ${type.minOccurs}</span>-->
                                            <#--<span>max: ${type.maxOccurs}</span>-->
                                            </li>
                                        </#list>
                                    </ul>
                                </#if>
                            </div>
                        </div>
                    </div>
                </li>
            </#list>
        </ul>
    </li>
</#list>
</ul>

<div class="wsdl-types">
<#list types?keys as name>
<div class="container-fluid <#if types[name].type == 0>complex<#else>simple</#if>">
    <#if types[name].type == 0>
        <div id="${name}" class="row">
            <div class="col-lg-3">
                <h4>${name}
                    <small class="complex">Complex type</small>
                </h4>
                <#if types[name].description ??>
                    <p>${types[name].description}</p>
                </#if>
                <#if types[name].schema??><#if types[name].schema != ""><a href="${types[name].schema}" target="_blank">schema link</a></#if></#if>
            </div>
            <div class="col-lg-9">
                <#if types[name].sequence??>
                    <table class="table table-hover">
                        <thead>
                        <th>name</th>
                        <th>type</th>
                        <th>cardinality</th>
                        <th>description</th>
                        </thead>
                        <tbody>
                            <#list types[name].sequence as el>
                            <tr>
                                <td class="col-lg-2">${el.name}</td>
                                <td class="col-lg-2">
                                    <#if el.nativeType>
                                        <#if el.typeName??>${el.typeName}</#if>
                                    <#else>
                                        <a href="#${el.typeName}">${el.typeName}</a>
                                    </#if>
                                </td>
                                <td class="col-lg-1">${el.minOccurs}..<#if el.maxOccurs??>${el.maxOccurs}<#else>*</#if></td>
                                <td class="col-lg-6"><#if el.description??>${el.description}</#if></td>
                            </tr>
                            </#list>
                        </tbody>
                    </table>
                </#if>
                <#if types[name].superTypes ??>
                    <p>
                        <strong>Extends: </strong>
                        <#list types[name].superTypes as superTypeName>
                            <a href="#${superTypeName}">${superTypeName}</a><span> </span>
                        </#list>
                    </p>
                </#if>
            </div>
        </div>
    <#--<div  class="panel panel-default">-->
    <#--<div class="panel-heading"></div>-->
    <#--<div class="panel-body">-->
    <#---->
    <#--</div>-->
    <#---->
    <#--</div>-->
    <#elseif types[name].type == 1>
        <div id="${name}" clas="row">
            <div class="col-lg-3">
                <h4>${name}
                    <#if types[name].base??><span>base: ${types[name].base}</span></#if>
                    <small class="simple">Simple type</small>
                </h4>
                <#if types[name].description ??>
                    <p>${types[name].description}</p>
                </#if>
                <#if types[name].schema??><#if types[name].schema != ""><a href="${types[name].schema}" target="_blank">schema link</a></#if></#if>
            </div>
            <div class="col-lg-9">
                <dl class="dl-horizontal">
                    <#if types[name].enumerations??>
                        <dt>Possible values</dt>
                        <dd>
                            <ul>
                                <#list types[name].enumerations as en>
                                    <li>${en}</li>
                                </#list>
                            </ul>
                        </dd>
                    </#if>
                    <#if types[name].minLength??>
                        <dt>Min length</dt>
                        <dd>${types[name].minLength}</dd>
                    </#if>
                    <#if types[name].minInclusive??>
                        <dt>Min inclusive</dt>
                        <dd>${types[name].minInclusive}</dd>
                    </#if>
                    <#if types[name].minExclusive??>
                        <dt>Min exclusive</dt>
                        <dd>${types[name].minExclusive}</dd>
                    </#if>
                    <#if types[name].maxLength??>
                        <dt>Max length</dt>
                        <dd>${types[name].maxLength}</dd>
                    </#if>
                    <#if types[name].maxInclusive??>
                        <dt>Max inclusive</dt>
                        <dd>${types[name].maxInclusive}</dd>
                    </#if>
                    <#if types[name].maxExclusive??>
                        <dt>Max exclusive</dt>
                        <dd>${types[name].maxExclusive}</dd>
                    </#if>
                    <#if types[name].length??>
                        <dt>Length</dt>
                        <dd>${types[name].length}</dd>
                    </#if>
                    <#if types[name].pattern??>
                        <dt>Pattern</dt>
                        <dd>${types[name].pattern}</dd>
                    </#if>
                    <#if types[name].whiteSpace??>
                        <dt>White space</dt>
                        <dd>${types[name].whiteSpace}</dd>
                    </#if>
                    <#if types[name].totalDigits??>
                        <dt>Total digits</dt>
                        <dd>${types[name].totalDigits}</dd>
                    </#if>
                    <#if types[name].fractionDigits??>
                        <dt>Fraction digits</dt>
                        <dd>${types[name].fractionDigits}</dd>
                    </#if>
                </dl>
            </div>
        </div>
    </#if>
</div>
</#list>
</div>

</body>
</html>