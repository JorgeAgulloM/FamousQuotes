package com.softyorch.famousquotes.ui.screens.onboading

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.core.commonComponents.AppVersionText
import com.softyorch.famousquotes.ui.core.commonComponents.TopBarStandard
import com.softyorch.famousquotes.ui.screens.home.components.AppIcon
import com.softyorch.famousquotes.ui.screens.home.components.ButtonApp
import com.softyorch.famousquotes.ui.screens.home.components.HeaderSubtitleApp
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.theme.AppColorSchema

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    leftHanded: Boolean,
    onUpNavigation: () -> Unit
) {

    val paddingTop = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(this).toDp() + 24.dp
    }

    val steps = listOf(
        "Presentación",
        "Interacciones de usuarios",
        "Menú principal",
        "Mis imagenes",
        "Agradecimientos"
    )
    var selectStep by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopBarStandard(
                modifier = modifier,
                paddingTop = paddingTop,
                leftHanded = leftHanded,
                textTitle = "OnBoarding",
                iconTitle = Icons.Default.LocalLibrary,
                onUpNavigation = onUpNavigation
            )
        },
        containerColor = AppColorSchema.background
    ) { dp ->

        val padding = PaddingValues(
            top = 8.dp + dp.calculateTopPadding(),
            start = 4.dp + dp.calculateStartPadding(LayoutDirection.Ltr),
            end = 4.dp + dp.calculateEndPadding(LayoutDirection.Ltr),
            bottom = 8.dp + dp.calculateBottomPadding() + Banner.heightBanner.dp
        )

        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .background(color = AppColorSchema.background, shape = MaterialTheme.shapes.large),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textButtonPrimary = if (selectStep == steps.size -1) "Finalizar"
            else "Siguiente"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectStep) {
                    0 -> StepZero(titleStep = steps[0])
                    1 -> StepOne(titleStep = steps[1])
                    2 -> StepTwo(titleStep = steps[2])
                    3 -> StepThree(titleStep = steps[3])
                    4 -> StepFour(titleStep = steps[4])
                }
            }

            ControlImageShow(steps = steps, selectStep = selectStep) { selectStep = it }
            ControlButtonsOnBoarding(
                textButtonPrimary = textButtonPrimary,
                onNext = {
                    selectStep += 1
                    if (selectStep >= steps.size) {
                        onUpNavigation()
                    }
                },
                onExit = onUpNavigation
            )
        }
    }
}

@Composable
fun StepZero(titleStep: String) {
    CardOnBoarding {
        SpacerHeight()
        TitleOnBoarding(text = titleStep)
        SpacerHeight()
        AppIcon()
        HeaderSubtitleApp()
        AppVersionText {}
        SpacerHeight()
        TextOnBoarding(text = "    A continuación te vamos a mostrar el funcionamiento de la aplicación.")
        SpacerHeight()
        TextOnBoarding(text = "    Te mostraremos el uso común de los controles básicos que encontrarás por las diferentes secciones de la app.")
        SpacerHeight()
        TextOnBoarding(text = "    Pulsa en siguiente o sobre los circulos para avanzar o retroceder.")
    }
}

@Composable
fun StepOne(titleStep: String) {
    CardOnBoarding {
        SpacerHeight()
        TitleOnBoarding(text = titleStep)
        SpacerHeight(32)
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Card(
                border = BorderStroke(4.dp, color = AppColorSchema.background)
            ) {
                Image(
                    painter = painterResource(R.drawable.image_ob_01),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        SpacerHeight()
        TextOnBoarding("    De arriba a abajo y de izquierda a derecha puedes encontrar:")
        SpacerHeight()
        TextOnBoarding("    Nombre del autor de la frase: \nAl pulsar sobre abriremos tu navegador y te mostgraremos una busqueda del mismo, para que obtengas más info si quieres.")
        SpacerHeight()
        TextOnBoarding("    Botón de Likes: \npulsado maracarás la frase como \"Me gusta\" y podrás ver los likes que tiene.")
        SpacerHeight()
        TextOnBoarding("    Botón de Favorito: \npulsado maracarás la frase como \"Favorita\" y podrás ver los favoritos que tiene.")
        SpacerHeight()
        TextOnBoarding("    Botón compartir: \nPodrás compartir como una imagen o solo la frase con quien tú quieras.")
        SpacerHeight()
        TextOnBoarding("    Icono Vistos: \nTendrás información sobre la cantidad de usuarios que han visto es frase.")
        SpacerHeight()
    }
}

@Composable
fun StepTwo(titleStep: String) {
    CardOnBoarding {
        Column {
            SpacerHeight()
            TitleOnBoarding(text = titleStep)
            SpacerHeight(32)
            Row(Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top) {
                    Card {
                        Image(
                            painter = painterResource(R.drawable.image_ob_02),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.width(75.dp)
                        )
                    }
                    SpacerHeight()
                    Card {
                        Image(
                            painter = painterResource(R.drawable.image_ob_03),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.width(75.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp, start = 16.dp)
                ) {
                    SpacerHeight()
                    TextOnBoarding("Menu Principal")
                    SpacerHeight(72)
                    TextOnBoarding("Cerrar Menu Principal")
                    SpacerHeight(32)
                    TextOnBoarding("Mis imagenes")
                    SpacerHeight(32)
                    TextOnBoarding("Cargar otra frase e imagen")
                    SpacerHeight(32)
                    TextOnBoarding("Descargar imagen actual")
                    SpacerHeight(32)
                    TextOnBoarding("Mostrar información de la app")
                    SpacerHeight(32)
                    TextOnBoarding("configuración")
                }
            }
        }
    }
}

@Composable
fun StepThree(titleStep: String) {
    CardOnBoarding {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            SpacerHeight()
            TitleOnBoarding(text = titleStep)
            SpacerHeight(32)
            Card(
                border = BorderStroke(4.dp, color = AppColorSchema.background)
            ) {
                Image(
                    painter = painterResource(R.drawable.image_ob_04),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            SpacerHeight()
            TextOnBoarding("    Aquí podrás ver aquellas imagenes que hayas visto, tus favoritas y a las que has dado un Like.")
            SpacerHeight()
            TextOnBoarding("    De arriba a abajo y de izquierda a derecha puedes encontrar:")
            SpacerHeight()
            TextOnBoarding("    Volver atrás: \nVolverás a la pantalla principal.")
            SpacerHeight()
            TextOnBoarding("    Filtrado por Likes: \nVerás las frases a las que has dado Like.")
            SpacerHeight()
            TextOnBoarding("    Filtrado por Vistas: \nPodrás ver todas aquellas frases que hayas visto. No te pierdas ninguna.")
            SpacerHeight()
            TextOnBoarding("    Filtrado por Favoritas: \nMostrará tus frases e imagenes favoritas.")
            SpacerHeight()
            TextOnBoarding("    Modificar orden: \nCambiarás el orden de las frases mostradas.")
            SpacerHeight()
            TextOnBoarding("    Pulsa sobre la frase que desees observar para ver sus detalles.")
        }
    }
}

@Composable
fun StepFour(titleStep: String) {
    CardOnBoarding {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            SpacerHeight()
            TitleOnBoarding(text = titleStep)
            SpacerHeight(32)
            TextOnBoarding("Finalización")
            SpacerHeight()
            TextOnBoarding("    Gracias por tu tiempo.")
            SpacerHeight()
            TextOnBoarding("    Ahora ya conoces el funcionamiento de nuestra app.")
            SpacerHeight()
            TextOnBoarding("    Esperamos que te guste y que te aporte aquello que estás buscando.")
        }
    }
}

@Composable
fun ControlImageShow(steps: List<String>, selectStep: Int, onClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        LazyRow {
            items(steps.size) {
                RadioButton(
                    selected = it == selectStep,
                    onClick = { onClick(it) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = AppColorSchema.primary,
                        unselectedColor = AppColorSchema.whiteSmoke
                    )
                )
            }
        }
    }
}

@Composable
fun ControlButtonsOnBoarding(
    textButtonPrimary: String,
    onNext: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val btnModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ButtonApp(
            modifier = btnModifier,
            text = textButtonPrimary,
            primary = true
        ) { onNext() }
        ButtonApp(
            modifier = btnModifier,
            text = "Salir"
        ) { onExit() }
    }
}

@Composable
fun TitleOnBoarding(text: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center
            ),
            color = AppColorSchema.text,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TextOnBoarding(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyLarge, color = AppColorSchema.text)
}

@Composable
fun CardOnBoarding(modifier: Modifier = Modifier, composable: @Composable () -> Unit) {

    val scrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 4.dp, start = 8.dp, end = 8.dp),
        colors = CardDefaults.cardColors(containerColor = AppColorSchema.smoke),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            composable()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    OnBoardingScreen(leftHanded = true) {}
}
