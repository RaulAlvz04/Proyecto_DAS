<?php

$DB_SERVER="db";
$DB_USER="root";
$DB_PASS="root";
$DB_DATABASE="DAS_GestorPelis_db";

$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

if (mysqli_connect_errno()) {
	echo 'Error de Conexión: ' . mysqli_connect_error();
	exit();
}

?>
