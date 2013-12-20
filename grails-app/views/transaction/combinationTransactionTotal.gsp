<%@ page import="com.tracking.Transaction" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'transaction.label', default: 'Transaction')}" />
		<title>Combination Total</title>
	</head>
	<body>
		<a href="#create-transaction" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<nav:render group="trackingTabs"/>
		</div>
		<div id="create-transaction" class="content scaffold-create" role="main">
			<h1>Enter the Total amount of the transactions</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:if test="${flash.errors}">
				<ul class="errors" role="alert">
					<g:each in="${flash.errors}" var="error">
						<li>${error.value}</li>
					</g:each>
				</ul>
			</g:if>
			<g:form action="combinationTransaction" >
				<fieldset class="form">
					<g:textField name="amount" value="${amount}"/>
					<g:hiddenField name="totalEntered" value="true"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="Submit" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
