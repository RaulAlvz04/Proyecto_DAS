<?php
include 'config.php';

$email = $_POST['email'];
$pass  = $_POST['password'];

// Comprobar si el usuario ya existe 
$buscar = "SELECT id FROM usuarios WHERE email = '$email'";
$resultado = mysqli_query($con, $buscar);

if (mysqli_num_rows($resultado) > 0) {
    // Si ya existe, error
    echo json_encode(["status" => "error", "message" => "El usuario ya existe"]);
} else {
    // Si no existe, insertar
    $insertar = "INSERT INTO usuarios (email, password) VALUES ('$email', '$pass')";
    
    if (mysqli_query($con, $insertar)) {
        echo json_encode(["status" => "ok", "message" => "Usuario creado"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Error al insertar"]);
    }
}

mysqli_close($con);
?>