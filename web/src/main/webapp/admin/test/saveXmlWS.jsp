<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>test save Form</title>
</head>
<body>
<h1>save form</h1>
<form action="../../ws/form" method="post">
User Id: <input type="text" name="userId"/><br/>
XML: <br/><textarea rows="10" name="xml"></textarea><br/>
<input type="submit"/>
</form>

<h1>save response</h1>
<form action="../../ws/answer" method="post">
Form Id: <input type="text" name="formId"/><br/>
User Id: <input type="text" name="userId"/><br/>
XML: <br/><textarea rows="10" name="xml"></textarea>
<input type="submit"/>
</form>
</body>
</html>