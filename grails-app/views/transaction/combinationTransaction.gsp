<%@ page import="com.tracking.Transaction" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'transaction.label', default: 'Transaction')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
		<style type="text/css">
			
			body{
				width: 1200px;
			}
			
			select {
    			max-width: 270px;
    		}
		</style>
	</head> 
	<body>
		<a href="#create-transaction" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<nav:render group="trackingTabs"/>
		</div>
		<div id="create-transaction" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /> (combination)</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${transactionInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${transactionInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<div style="width:40%; float:left;">
				<g:set var="formAction" value="createCombinationTransaction"/>
				<g:if test="${totalAmount==0}">
					<g:set var="formAction" value="saveCombinationTransactions"/>
				</g:if>
				<g:form action="${formAction}" >
					<fieldset class="form">
						<g:render template="combinationTransactionForm"/>
					</fieldset>
					<fieldset class="buttons">
						<g:hiddenField name="dateLock" value = "true"/>
						<g:if test="${totalAmount>0}">
							<g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
						</g:if>
						<g:else>
							<g:link class="save" action="saveCombinationTransactions">Save</g:link>
						</g:else>
					</fieldset>
				</g:form>
			</div>
			<div style = "width:58%; float:right; padding-right:20px">
			<h3>Pending Transactions</h3>
			<table>
						<thead>
							<tr>
								
								<td></td>
								
								<g:sortableColumn property="category" title="${message(code: 'transaction.category.label', default: 'Category')}" />
							
								<g:sortableColumn property="name" title="${message(code: 'transaction.name.label', default: 'Name')}" />
							
								<g:sortableColumn property="amount" title="${message(code: 'transaction.amount.label', default: 'Amount')}" />
							
								<th><g:message code="transaction.account.label" default="Account" /></th>
							
							</tr>
						</thead>
						<tbody>
						
						<g:each in="${session.newTransactions}" status="i" var="transactionMap">
							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}" onclick="populateForm('${transactionMap.value.id}'); disableElement('create'); enableElement('delete'); enableElement('update'); ">
								
								<td><g:link action="removeTransactionFromCombo" params="[transactionKey:transactionMap.key,amount:totalAmount]">Remove</g:link></td>
							
								<td>${transactionMap.value.category}</td>
							
								<td>${transactionMap.value.name}</td>
							
								<td><g:formatNumber number="${transactionMap.value.amount}" type="currency" currencyCode="USD"/></td>
							
								<td>${transactionMap.value.account}</td>
							</tr>
						</g:each>
						</tbody>
					</table>
			</div>
		</div>
	</body>
</html>
