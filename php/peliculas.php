<?php
include 'config.php';

// Determinamos qué función ejecutar
$accion = $_GET['accion'];

switch($accion) {
    case 'insertar':
        insertar($con);
        break;
    case 'eliminar':
        eliminar($con);
        break;
    case 'actualizar':
        actualizar($con);
        break;
    case 'favoritos':
        getFavoritos($con);
        break;
    case 'pendientes':
        getPendientes($con);
        break;
    case 'por_usuario':
        getPelisPorUsuario($con);
        break;
    case 'por_id':
        getPeliPorId($con);
        break;
    default:
        echo json_encode(["status" => "error", "message" => "Accion no reconocida"]);
}

// Obtener pelis de un usuario
function getPelisPorUsuario($con) {
    $idUsuario = $_GET['idUsuario'];
    $sql = "SELECT * FROM peliculas WHERE idUsuario = '$idUsuario'";
    ejecutarSelect($con, $sql);
}

// Obtener favoritas
function getFavoritos($con) {
    $idUsuario = $_GET['idUsuario'];
    $sql = "SELECT * FROM peliculas WHERE idUsuario = '$idUsuario' AND esFavorito = 1";
    ejecutarSelect($con, $sql);
}

// Obtener pendientes
function getPendientes($con) {
    $idUsuario = $_GET['idUsuario'];
    $sql = "SELECT * FROM peliculas WHERE idUsuario = '$idUsuario' AND esPendiente = 1";
    ejecutarSelect($con, $sql);
}

// Obtener una peli concreta
function getPeliPorId($con) {
    $idPeli = $_GET['idPeli'];
    $sql = "SELECT * FROM peliculas WHERE id = '$idPeli'";
    $res = mysqli_query($con, $sql);
    echo json_encode(mysqli_fetch_assoc($res));
}

// Insertar
function insertar($con) {
    // Aseguramos que recibimos los datos
    $titulo = isset($_POST['titulo']) ? $_POST['titulo'] : '';
    $anno = isset($_POST['anno']) ? $_POST['anno'] : '';
    $genero = isset($_POST['genero']) ? $_POST['genero'] : '';
    $valoracion = isset($_POST['valoracion']) ? $_POST['valoracion'] : 0;
    $esPendiente = isset($_POST['esPendiente']) ? $_POST['esPendiente'] : 0;
    $idUsuario = isset($_POST['idUsuario']) ? $_POST['idUsuario'] : 0;

    $sql = "INSERT INTO peliculas (titulo, anno, genero, valoracion, opinion, esFavorito, imagen, esPendiente, idUsuario) 
            VALUES ('$titulo', '$anno', '$genero', '$valoracion', NULL, 0, NULL, '$esPendiente', '$idUsuario')";
    
    if(mysqli_query($con, $sql)) {
        echo json_encode(["status" => "ok"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($con)]);
    }
}

// Eliminar
function eliminar($con) {
    $idPeli = $_POST['idPeli'];
    $sql = "DELETE FROM peliculas WHERE id = '$idPeli'";
    if(mysqli_query($con, $sql)) {
        echo json_encode(["status" => "ok"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($con)]);
    }
}

function actualizar($con) {
    $id = $_POST['id']; 
    $titulo = $_POST['titulo'];
    $anno = $_POST['anno'];
    $genero = $_POST['genero'];
    $valoracion = $_POST['valoracion'];
    $opinion = $_POST['opinion'];
    $esFavorito = $_POST['esFavorito']; 
    $esPendiente = $_POST['esPendiente']; 
    $imagen = $_POST['imagen'];

    // Preparamos la consulta SQL
    $sql = "UPDATE peliculas SET 
            titulo = '$titulo', 
            anno = '$anno', 
            genero = '$genero', 
            valoracion = '$valoracion', 
            opinion = '$opinion', 
            esFavorito = '$esFavorito', 
            imagen = '$imagen', 
            esPendiente = '$esPendiente' 
            WHERE id = '$id'";

    if(mysqli_query($con, $sql)) {
        echo json_encode(["status" => "ok"]);
    } else {
        echo json_encode([
            "status" => "error", 
            "message" => mysqli_error($con)
        ]);
    }
}

// --- FUNCIÓN AUXILIAR PARA SELECTS ---
function ejecutarSelect($con, $sql) {
    $result = mysqli_query($con, $sql);
    $rows = array();
    while($r = mysqli_fetch_assoc($result)) {
        $rows[] = $r;
    }
    echo json_encode($rows);
}

mysqli_close($con);
?>