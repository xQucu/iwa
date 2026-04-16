<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Page Title</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
    <h1>${message}</h1>

    <h2>Student</h2>
    <form:form method="post" action="addStudent.html" modelAttribute="student">
        <form:label path="firstName">Firstname</form:label>
        <form:input path="firstName" />
        <br>
        <form:label path="lastName">Lastname</form:label>
        <form:input path="lastName" />
        <br>
        <form:label path="email">Email</form:label>
        <form:input path="email" />
        <br>
        <form:label path="number">Telephone</form:label>
        <form:input path="number" />
        <br>
        <button type="submit">Add Student</button>
    </form:form>

</body>
</html>
