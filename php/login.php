<?php
include 'config.php';

$email = $_POST['email'];
$pass  = $_POST['password'];

// Consultar si el usuario existe
$sql = "SELECT id, email FROM usuarios WHERE email = '$email' AND password = '$pass'";
$result = mysqli_query($con, $sql);

if ($row = mysqli_fetch_assoc($result)) {
    // Credenciales correctas
    echo json_encode([
        "status" => "ok",
        "id" => (int)$row['id'],
        "email" => $row['email']
    ]);
} else {
    // Credenciales incorrectas
    echo json_encode([
        "status" => "error",
        "message" => "Usuario o contraseña incorrectos"
    ]);
}

mysqli_close($con);
?>