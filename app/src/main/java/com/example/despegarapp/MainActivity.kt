package com.example.despegarapp

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.*
import androidx.navigation.navArgument
import com.example.despegarapp.ui.theme.DespegarAppTheme
import java.text.SimpleDateFormat

sealed class Pantalla(val ruta: String) {
    // pantallas
    object Principal : Pantalla("principal")
    object Vuelos : Pantalla("vuelos")
    object Confirmacion : Pantalla("confirmacion")
}

//objeto para los datos de usuario
object DatosUsuario {
    var nombre: String = ""
    var pasaporte: String = ""
    var destino: String = ""
    var fecha: String = ""
    var hora: String = ""
}
//fuente de informacion https://youtu.be/glyqjzkc4fk

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //selecionamos el tema
            AerolineaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AerolineaApp()//funcion que intregra las tres ventanas de navegacion
                }
            }
        }
    }
}
@Composable
fun AerolineaApp(){
    //establecemos el controlador de la nevegacion
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Pantalla.Principal.ruta)
    {
        composable(Pantalla.Principal.ruta) { PrincipalScreen(navController)
        }
        composable(Pantalla.Vuelos.ruta) { VuelosScreen(navController)
        }
        composable(Pantalla.Confirmacion.ruta) {
            ConfirmacionScreen(
                //datos a confirmar
                navController = navController,
                nombre = DatosUsuario.nombre,
                numeroPasaporte = DatosUsuario.pasaporte,
                destino = DatosUsuario.destino,
                fecha = DatosUsuario.fecha,
                hora = DatosUsuario.hora
            )
        }
    }
}

@Composable
fun PrincipalScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var numeroPasaporte by remember { mutableStateOf("") }
    var esValido by remember { mutableStateOf(false) } //boolean para que se cumpla campos no vacios y contenidos de caracteres

    Box(contentAlignment = Alignment.Center, modifier =
    Modifier.fillMaxSize().padding(16.dp)){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                "Bienvenido a Aerolíneas Despegar",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            //campo nombre
            OutlinedTextField(value = nombre,
                onValueChange = {
                    nombre = it
                    esValido = nombre.isNotEmpty() && numeroPasaporte.length >= 8 },//validacion texto y longuitud
                    label = {Text("Nombre Completo")}
            )
            //campo pasaporte
            OutlinedTextField(value = numeroPasaporte,
                onValueChange = {
                    numeroPasaporte = it
                    esValido = nombre.isNotEmpty() && numeroPasaporte.length >= 8 }, //para que se cumpla la condicion descrita
                    label = {Text("Numero de Pasaporte")})

            Button(
                onClick = {
                    DatosUsuario.nombre = nombre
                    DatosUsuario.pasaporte = numeroPasaporte
                    navController.navigate(Pantalla.Vuelos.ruta)
                          },
                enabled = esValido
            ) {
                Text("Siguiente")
            }

        }//fin Colum
    }//fin box
}//fin PrincipalScreen

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VuelosScreen(navController: NavController) {
    //variables de destinos
    val destinos = listOf("París", "Nueva York", "Tokio", "Moscu", "Cancun", "Bonaire", "Rio de Janeiro")
    var destinoSeleccionado by remember { mutableStateOf("") }
    //varibales de fechas
    var fechaSeleccionada by remember { mutableStateOf("") }
    //variables de horas
    val horasDisponible = listOf("3:30 AM", "8:30 AM", "12:00 PM", "5:30 PM", "8:50 PM" )
    var horaSeleccionada by remember { mutableStateOf("") }
    //variables de menus desplegables
    var expandirMenuDestino by remember { mutableStateOf(false) }
    var expandirMenuHoras by remember { mutableStateOf(false)}
    val context = LocalContext.current // Contexto para el DatePicker
    val calendar = Calendar.getInstance() // Calendario para el DatePicker

    val datePickerDialog = DatePickerDialog(
        context,
        //definimos el formato de fecha
        { _, year, month, dayOfMonth,  ->
            calendar.set(year, month, dayOfMonth)
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            fechaSeleccionada = simpleDateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(contentAlignment = Alignment.Center, modifier =
    Modifier.fillMaxSize().padding(16.dp)){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)) {

            Text("Seleccione su destino y fecha", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de destino
            OutlinedTextField(
                value = destinoSeleccionado,
                onValueChange = { },
                label = { Text("Destino") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    //.clickable { expandirMenu = true }
            )

            //boton para sleccionar el destino
            Button( onClick = { expandirMenuDestino=true }) {
                Text("Seleccionar destino")
            }
            // Menú desplegable para destinos
            DropdownMenu(
                expanded = expandirMenuDestino,
                onDismissRequest = { expandirMenuDestino = false },
                modifier = Modifier.fillMaxWidth()
            ){
                destinos.forEach { destino ->
                    DropdownMenuItem(
                        text = { Text(destino) },
                        onClick = {
                            destinoSeleccionado = destino
                            expandirMenuDestino = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))


            OutlinedTextField(value = fechaSeleccionada,
                onValueChange = { },
                label = {Text("Fecha de salida")},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth())
                    //.clickable { DatePickerDialog.show()})

            Spacer(modifier = Modifier.height(10.dp))

            // Botón para seleccionar fecha
            Button(onClick = { datePickerDialog.show() }) {
                Text("Seleccionar Fecha")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Campo Hora
            OutlinedTextField(value = horaSeleccionada,
                onValueChange = { },
                label = {Text("Hora de salida")},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth())

            Spacer(modifier = Modifier.height(10.dp))

            //boton para selecionar la hora
            Button( onClick = { expandirMenuHoras=true }) {
                Text("Hora salida")
            }

            DropdownMenu(
                expanded = expandirMenuHoras,
                onDismissRequest = { expandirMenuHoras = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ){
                horasDisponible.forEach { hora ->
                    DropdownMenuItem(
                        text = { Text(hora) },
                        onClick = {
                            horaSeleccionada = hora
                            expandirMenuHoras = false
                        }

                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botón para reservar vuelo
            Button(
                onClick = {
                    DatosUsuario.destino = destinoSeleccionado
                    DatosUsuario.fecha = fechaSeleccionada
                    DatosUsuario.hora = horaSeleccionada
                    // Usar el formato correcto para la navegación con parámetros
                    navController.navigate(Pantalla.Confirmacion.ruta)
                },
                enabled = destinoSeleccionado.isNotEmpty() && fechaSeleccionada.isNotEmpty() && horaSeleccionada.isNotEmpty()
            ) {
                Text("Reservar Vuelo")
            }
        }
    }
}

@Composable
fun ConfirmacionScreen(
    navController: NavController,
    nombre: String,
    numeroPasaporte: String,
    destino: String,
    fecha: String,
    hora: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Resumen de Reserva",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "Datos del Pasajero",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Nombre: $nombre")
                    Text("Número de Pasaporte: $numeroPasaporte")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Datos del Vuelo",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Destino: $destino")
                    Text("Fecha: $fecha")
                    Text("Hora de salida: $hora")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigateUp() }
                ) {
                    Text("Volver")
                }

                Button(
                    onClick = {
                        // Reiniciar y volver al inicio
                        navController.navigate(Pantalla.Principal.ruta) {
                            popUpTo(Pantalla.Principal.ruta) { inclusive = true }
                        }
                    }
                ) {
                    Text("Finalizar")
                }
            }
        }
    }
}
// funcion para que la app se ejecute(AppNavigation)
@Composable
fun AerolineaTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}