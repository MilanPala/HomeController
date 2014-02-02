<?php

$action = isset($_GET['action']) ? $_GET['action'] : NULL;
$value = isset($_GET['value']) ? $_GET['value'] : NULL;

$config = array();
if (file_exists('config.ini.local')) $config = parse_ini_file('config.ini.local');
if (!$config)
{
	$config['port'] = 8080;
}

$url = 'http://localhost:' . $config['port'];

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

	print_r($config);

	print_r($_GET);
}
exit;
