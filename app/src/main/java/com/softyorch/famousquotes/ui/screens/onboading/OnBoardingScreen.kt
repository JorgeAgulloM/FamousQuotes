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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.softyorch.famousquotes.R
import com.softyorch.famousquotes.ui.admob.Banner
import com.softyorch.famousquotes.ui.core.commonComponents.AppVersionText
import com.softyorch.famousquotes.ui.core.commonComponents.TopBarStandard
import com.softyorch.famousquotes.ui.screens.home.components.AppIcon
import com.softyorch.famousquotes.ui.screens.home.components.HeaderSubtitleApp
import com.softyorch.famousquotes.ui.screens.home.components.SpacerHeight
import com.softyorch.famousquotes.ui.screens.onboading.components.CardOnBoarding
import com.softyorch.famousquotes.ui.screens.onboading.components.ControlButtonsOnBoarding
import com.softyorch.famousquotes.ui.screens.onboading.components.ControlImageShow
import com.softyorch.famousquotes.ui.screens.onboading.components.TextOnBoarding
import com.softyorch.famousquotes.ui.screens.onboading.components.TitleOnBoarding
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
        stringResource(R.string.on_boarding_list_steps_presentation),
        stringResource(R.string.on_boarding_list_steps_interaction_users),
        stringResource(R.string.on_boarding_list_steps_menu),
        stringResource(R.string.on_boarding_list_steps_my_images),
        stringResource(R.string.on_boarding_list_steps_acknowledgements)
    )
    var selectStep by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopBarStandard(
                modifier = modifier,
                paddingTop = paddingTop,
                leftHanded = leftHanded,
                textTitle = stringResource(R.string.on_boarding_top_bar_title),
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
            val textButtonPrimary = if (selectStep == steps.size - 1)
                stringResource(R.string.on_boarding_button_action_principal_finish)
            else stringResource(R.string.on_boarding_button_action_principal_next)

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
private fun StepZero(titleStep: String) {
    CardOnBoarding {
        TitleOnBoarding(text = titleStep)
        AppIcon()
        HeaderSubtitleApp()
        AppVersionText {}
        TextOnBoarding(text = stringResource(R.string.on_boarding_step_one_line_one))
        TextOnBoarding(text = stringResource(R.string.on_boarding_step_one_line_two))
        TextOnBoarding(text = stringResource(R.string.on_boarding_step_one_line_three))
    }
}

@Composable
private fun StepOne(titleStep: String) {
    CardOnBoarding {
        TitleOnBoarding(text = titleStep)
        SpacerHeight()
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
        TextOnBoarding(stringResource(R.string.on_boarding_step_two_line_one))
        TextOnBoarding(
            stringResource(R.string.on_boarding_step_two_line_two_title),
            stringResource(R.string.on_boarding_step_two_line_two_description)
        )
        TextOnBoarding(
            stringResource(R.string.on_boarding_step_two_line_three_title),
            stringResource(R.string.on_boarding_step_two_line_three_description)
        )
        TextOnBoarding(
            stringResource(R.string.on_boarding_step_two_line_four_title),
            stringResource(R.string.on_boarding_step_two_line_four_description)
        )
        TextOnBoarding(
            stringResource(R.string.on_boarding_step_two_line_five_title),
            stringResource(R.string.on_boarding_step_two_line_five_description)
        )
        TextOnBoarding(
            stringResource(R.string.on_boarding_step_two_line_six_title),
            stringResource(R.string.on_boarding_step_two_line_six_description)
        )
    }
}

@Composable
private fun StepTwo(titleStep: String) {
    CardOnBoarding {
        Column {
            TitleOnBoarding(text = titleStep)
            SpacerHeight()
            Row(Modifier.fillMaxSize()) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
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
                    TextOnBoarding(stringResource(R.string.on_boarding_step_three_line_one))
                    SpacerHeight(56)
                    TextOnBoarding(stringResource(R.string.on_boarding_step_three_line_two))
                    SpacerHeight()
                    TextOnBoarding(stringResource(R.string.on_boarding_step_three_line_three))
                    SpacerHeight()
                    TextOnBoarding(stringResource(R.string.on_boarding_step_three_line_four))
                    SpacerHeight()
                    TextOnBoarding(stringResource(R.string.on_boarding_step_three_line_five))
                    SpacerHeight()
                    TextOnBoarding(stringResource(R.string.on_boarding_step_three_line_six))
                    SpacerHeight()
                    TextOnBoarding(stringResource(R.string.on_boarding_step_three_line_seven))
                }
            }
        }
    }
}

@Composable
private fun StepThree(titleStep: String) {
    CardOnBoarding {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            TitleOnBoarding(text = titleStep)
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
            TextOnBoarding(stringResource(R.string.on_boarding_step_four_line_one))
            TextOnBoarding(stringResource(R.string.on_boarding_step_four_line_two))
            TextOnBoarding(
                stringResource(R.string.on_boarding_step_four_line_three_title),
                stringResource(R.string.on_boarding_step_four_line_three_description)
            )
            TextOnBoarding(
                stringResource(R.string.on_boarding_step_four_line_four_title),
                stringResource(R.string.on_boarding_step_four_line_four_description)
            )
            TextOnBoarding(
                stringResource(R.string.on_boarding_step_four_line_five_title),
                stringResource(R.string.on_boarding_step_four_line_five_description)
            )
            TextOnBoarding(
                stringResource(R.string.on_boarding_step_four_line_six_title),
                stringResource(R.string.on_boarding_step_four_line_six_description)
            )
            TextOnBoarding(
                stringResource(R.string.on_boarding_step_four_line_seven_title),
                stringResource(R.string.on_boarding_step_four_line_seven_description)
            )
            TextOnBoarding(stringResource(R.string.on_boarding_step_four_line_eight))
        }
    }
}

@Composable
private fun StepFour(titleStep: String) {
    CardOnBoarding {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TitleOnBoarding(text = titleStep)
            SpacerHeight()
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = AppColorSchema.secondary,
                modifier = Modifier.size(100.dp)
            )
            TextOnBoarding(stringResource(R.string.on_boarding_step_five_line_one))
            TextOnBoarding(stringResource(R.string.on_boarding_step_five_line_two))
            TextOnBoarding(stringResource(R.string.on_boarding_step_five_line_three))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreview() {
    OnBoardingScreen(leftHanded = true) {}
}
