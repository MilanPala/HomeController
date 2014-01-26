<?php

$action = isset($_GET['action']) ? $_GET['action'] : NULL;
$value = isset($_GET['value']) ? $_GET['value'] : NULL;

$url = 'http://localhost:8085';

$data = NULL;

switch ($action)
{
	case 'status':
		$data = @file_get_contents($url . '/');
		break;

	case 'r':
		$data = @file_get_contents($url . '/r/' . $value);
		break;

	case 's':
		$data = @file_get_contents($url . '/s/' . $value);
		break;

	default:
		$data = NULL;
}

if ($data)
{
	header('Content-type: application/json');
	echo $data;
}
else
{
	header("HTTP/1.1 404 Not Found");
	header('Content-type: text/plain');

	var_dump($_GET);

	var_dump($_SERVER);
}
exit;
