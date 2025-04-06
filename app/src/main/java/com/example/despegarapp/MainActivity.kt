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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "principal") {
                    composable("principal") { PrincipalScreen(navController) }
                    composable("vuelos") { VuelosScreen(navController) }
                    //composable("confirmacion") { ConfirmacionScreen(navController) }
                    composable(
                        "confirmation/{nombre}/{numeroPasaporte}/{destino}/{fecha}",
                        arguments = listOf(
                            navArgument("nombre") { type = NavType.StringType },
                            navArgument("numeroPasaporte") { type = NavType.StringType },
                            navArgument("destino") { type = NavType.StringType },
                            navArgument("fecha") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        ConfirmacionScreen(
                            navController = navController,
                            nombre = backStackEntry.arguments?.getString("nombre") ?: "",
                            numeroPasaporte = backStackEntry.arguments?.getString("numeroPasaporte") ?: "",
                            destino = backStackEntry.arguments?.getString("destino") ?: "",
                            fecha = backStackEntry.arguments?.getString("fecha") ?: ""
                        )
                    }

                }

        }
    }
}
@Composable
fun PrincipalScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var numeroPasaporte by remember { mutableStateOf("") }
    var esValido by remember { mutableStateOf(false) }

    Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier =
    Modifier.fillMaxSize()){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally) {

            OutlinedTextField(value = nombre,
                onValueChange = {
                    nombre = it
                    esValido = nombre.isNotEmpty() && numeroPasaporte.length >= 8 },//validacion texto y longuitud
                    label = {Text("Nombre Completo")})
            OutlinedTextField(value = numeroPasaporte,
                onValueChange = {
                    numeroPasaporte = it
                    esValido = nombre.isNotEmpty() && numeroPasaporte.length >= 8 },
                    label = {Text("Numero de Pasaporte")})

            Button(
                onClick = { navController.navigate("vuelos") },
                enabled = esValido
            ) {
                Text("Siguiente")
            }

        }//fin Colum
    }//fin box
}//fin PrincipalScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VuelosScreen(navController: NavController) {
    val destinos = listOf("París", "Nueva York", "Tokio")
    var destinoSeleccionado by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf("") }
    var expandirMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current // Contexto para el DatePicker
    val calendar = Calendar.getInstance() // Calendario para el DatePicker
    val DatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            fechaSeleccionada = calendar.time.toString()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier =
    Modifier.fillMaxSize()){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally){

            Text("Seleccione su destino y fecha", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = destinoSeleccionado,
                onValueChange = { },
                label = {Text("Destino")},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandirMenu = true })


            DropdownMenu(
                expanded = expandirMenu,//destinoSeleccionado.isEmpty(),
                onDismissRequest = { expandirMenu = false },
            ){
                destinos.forEach { destino->
                    DropdownMenuItem (
                        text = { Text(destino) },
                        onClick = { destinoSeleccionado = destino
                                    expandirMenu =false})
                }//fin forEach

                Spacer(modifier = Modifier.height(8.dp))


                OutlinedTextField(value = fechaSeleccionada,
                    onValueChange = { },
                    label = {Text("Fecha de salida")},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { DatePickerDialog.show()})

                Text("Seleccione la fecha de salida")
                Button(onClick = { expandirMenu = true }) {
                    Text("Seleccionar Fecha")
                }

                /*if (showDatePicker){
                    DatePickerDialog(context, { _, year, month, dayOfMonth ->
                        fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
                        showDatePicker = false
                    }, year, month, day).show()
                }*/

                Button(
                    onClick = { navController.navigate("confirmacion/${destinoSeleccionado}/${fechaSeleccionada}") },
                    enabled = destinoSeleccionado.isNotEmpty() && fechaSeleccionada.isNotEmpty()
                ) {
                    Text("Reservar Vuelo")
                }
            }
        }//fin Column
    }//fin box
}//fin vuelosScreen

/*fun DropdownMenuItem(onClick: () -> Unit, interactionSource: @Composable () -> Unit) {
    TODO("Not yet implemented")
}*/

@Composable
fun ConfirmacionScreen(navController: NavController, nombre: String,
                       numeroPasaporte: String, destino: String, fecha: String) {

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Resumen de Reserva", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(40.dp))
            Text("Nombre: $nombre", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(40.dp))
            Text("Pasaporte: $numeroPasaporte", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(40.dp))
            Text("Destino: $destino", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(40.dp))
            Text("Fecha: $fecha", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Finalizar")
            }
        }//fin column
    }//fin box
}//fin comfirmacionScreen




