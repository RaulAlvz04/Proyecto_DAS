<?php
include 'config.php';

if ($_POST['accion'] == 'subir_foto') {
    $idUsuario = $_POST['idUsuario'];
    $img64 = $_POST['imagen'];

    // Decodificar el String Base64
    $datosDecodificados = base64_decode($img64);

    // Crear un nombre único para el archivo
    $nombreFichero = "user_" . $idUsuario . ".jpg";
    $rutaDestino = "imagenesPerfil/" . $nombreFichero;

    // Crear el archivo en el servidor
    if (file_put_contents($rutaDestino, $datosDecodificados)) {
        
        // Actualizar la base de datos con el nombre del archivo
        $sql = "UPDATE usuarios SET foto = '$nombreFichero' WHERE id = $idUsuario";
        error_log("Consulta a ejecutar: " . $sql);

        if (mysqli_query($con, $sql)) {
            echo $nombreFichero;
        } else {
        
            $error_detalle = mysqli_error($con);
            error_log("DETALLE DEL FALLO: " . $error_detalle);
            echo "error_db: " . $error_detalle;
        }

        echo $nombreFichero; // Devolvemos el nombre a Android
    } else {
        header("HTTP/1.1 500 Internal Server Error");
        echo "Error al crear el archivo .jpg";
    }
}
?>