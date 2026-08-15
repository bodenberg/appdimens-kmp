/**
 * EN Routes Compose demo dimensions to the selected scaling strategy (same formulas as each library package).
 * PT Encaminha as dimensões do demo Compose para a estratégia escolhida (mesmas fórmulas de cada pacote).
 */
package com.example.app.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Orientation
import com.appdimens.kmp.common.UiModeType
import com.appdimens.kmp.compose.auto.asdp
import com.appdimens.kmp.compose.auto.asdpa
import com.appdimens.kmp.compose.auto.asdpLh
import com.appdimens.kmp.compose.auto.asdpLw
import com.appdimens.kmp.compose.auto.asdpMode
import com.appdimens.kmp.compose.auto.asdpPh
import com.appdimens.kmp.compose.auto.asdpQualifier
import com.appdimens.kmp.compose.auto.asdpRotate
import com.appdimens.kmp.compose.auto.asdpRotatePlain
import com.appdimens.kmp.compose.auto.asdpScreen
import com.appdimens.kmp.compose.auto.asdpModePlain
import com.appdimens.kmp.compose.auto.asdpQualifierPlain
import com.appdimens.kmp.compose.auto.asdpScreenPlain
import com.appdimens.kmp.compose.auto.ahdp
import com.appdimens.kmp.compose.auto.ahdpLw
import com.appdimens.kmp.compose.auto.autoDp
import com.appdimens.kmp.compose.auto.awdp
import com.appdimens.kmp.compose.auto.awdpLh
import com.appdimens.kmp.compose.density.densityDp
import com.appdimens.kmp.compose.density.dsdpa
import com.appdimens.kmp.compose.density.dhdp
import com.appdimens.kmp.compose.density.dhdpLw
import com.appdimens.kmp.compose.density.dsdp
import com.appdimens.kmp.compose.density.dsdpLh
import com.appdimens.kmp.compose.density.dsdpLw
import com.appdimens.kmp.compose.density.dsdpMode
import com.appdimens.kmp.compose.density.dsdpPh
import com.appdimens.kmp.compose.density.dsdpQualifier
import com.appdimens.kmp.compose.density.dsdpRotate
import com.appdimens.kmp.compose.density.dsdpRotatePlain
import com.appdimens.kmp.compose.density.dsdpScreen
import com.appdimens.kmp.compose.density.dsdpModePlain
import com.appdimens.kmp.compose.density.dsdpQualifierPlain
import com.appdimens.kmp.compose.density.dsdpScreenPlain
import com.appdimens.kmp.compose.density.dwdp
import com.appdimens.kmp.compose.density.dwdpLh
import com.appdimens.kmp.compose.diagonal.diagonalDp
import com.appdimens.kmp.compose.diagonal.dgsdpa
import com.appdimens.kmp.compose.diagonal.dghdp
import com.appdimens.kmp.compose.diagonal.dghdpLw
import com.appdimens.kmp.compose.diagonal.dgsdp
import com.appdimens.kmp.compose.diagonal.dgsdpLh
import com.appdimens.kmp.compose.diagonal.dgsdpLw
import com.appdimens.kmp.compose.diagonal.dgsdpMode
import com.appdimens.kmp.compose.diagonal.dgsdpPh
import com.appdimens.kmp.compose.diagonal.dgsdpQualifier
import com.appdimens.kmp.compose.diagonal.dgsdpRotate
import com.appdimens.kmp.compose.diagonal.dgsdpRotatePlain
import com.appdimens.kmp.compose.diagonal.dgsdpScreen
import com.appdimens.kmp.compose.diagonal.dgsdpModePlain
import com.appdimens.kmp.compose.diagonal.dgsdpQualifierPlain
import com.appdimens.kmp.compose.diagonal.dgsdpScreenPlain
import com.appdimens.kmp.compose.diagonal.dgwdp
import com.appdimens.kmp.compose.diagonal.dgwdpLh
import com.appdimens.kmp.compose.fill.fillDp
import com.appdimens.kmp.compose.fill.flsdpa
import com.appdimens.kmp.compose.fill.flhdp
import com.appdimens.kmp.compose.fill.flhdpLw
import com.appdimens.kmp.compose.fill.flsdp
import com.appdimens.kmp.compose.fill.flsdpLh
import com.appdimens.kmp.compose.fill.flsdpLw
import com.appdimens.kmp.compose.fill.flsdpMode
import com.appdimens.kmp.compose.fill.flsdpPh
import com.appdimens.kmp.compose.fill.flsdpQualifier
import com.appdimens.kmp.compose.fill.flsdpRotate
import com.appdimens.kmp.compose.fill.flsdpRotatePlain
import com.appdimens.kmp.compose.fill.flsdpScreen
import com.appdimens.kmp.compose.fill.flsdpModePlain
import com.appdimens.kmp.compose.fill.flsdpQualifierPlain
import com.appdimens.kmp.compose.fill.flsdpScreenPlain
import com.appdimens.kmp.compose.fill.flwdp
import com.appdimens.kmp.compose.fill.flwdpLh
import com.appdimens.kmp.compose.fit.fitDp
import com.appdimens.kmp.compose.fit.ftsdpa
import com.appdimens.kmp.compose.fit.fthdp
import com.appdimens.kmp.compose.fit.fthdpLw
import com.appdimens.kmp.compose.fit.ftsdp
import com.appdimens.kmp.compose.fit.ftsdpLh
import com.appdimens.kmp.compose.fit.ftsdpLw
import com.appdimens.kmp.compose.fit.ftsdpMode
import com.appdimens.kmp.compose.fit.ftsdpPh
import com.appdimens.kmp.compose.fit.ftsdpQualifier
import com.appdimens.kmp.compose.fit.ftsdpRotate
import com.appdimens.kmp.compose.fit.ftsdpRotatePlain
import com.appdimens.kmp.compose.fit.ftsdpScreen
import com.appdimens.kmp.compose.fit.ftsdpModePlain
import com.appdimens.kmp.compose.fit.ftsdpQualifierPlain
import com.appdimens.kmp.compose.fit.ftsdpScreenPlain
import com.appdimens.kmp.compose.fit.ftwdp
import com.appdimens.kmp.compose.fit.ftwdpLh
import com.appdimens.kmp.compose.fluid.fluidDp
import com.appdimens.kmp.compose.fluid.fsdpa
import com.appdimens.kmp.compose.fluid.fhdp
import com.appdimens.kmp.compose.fluid.fhdpLw
import com.appdimens.kmp.compose.fluid.fsdp
import com.appdimens.kmp.compose.fluid.fsdpLh
import com.appdimens.kmp.compose.fluid.fsdpLw
import com.appdimens.kmp.compose.fluid.fsdpMode
import com.appdimens.kmp.compose.fluid.fsdpPh
import com.appdimens.kmp.compose.fluid.fsdpQualifier
import com.appdimens.kmp.compose.fluid.fsdpRotate
import com.appdimens.kmp.compose.fluid.fsdpRotatePlain
import com.appdimens.kmp.compose.fluid.fsdpScreen
import com.appdimens.kmp.compose.fluid.fsdpModePlain
import com.appdimens.kmp.compose.fluid.fsdpQualifierPlain
import com.appdimens.kmp.compose.fluid.fsdpScreenPlain
import com.appdimens.kmp.compose.fluid.fwdp
import com.appdimens.kmp.compose.fluid.fwdpLh
import com.appdimens.kmp.compose.interpolated.interpolatedDp
import com.appdimens.kmp.compose.interpolated.isdpa
import com.appdimens.kmp.compose.interpolated.ihdp
import com.appdimens.kmp.compose.interpolated.ihdpLw
import com.appdimens.kmp.compose.interpolated.isdp
import com.appdimens.kmp.compose.interpolated.isdpLh
import com.appdimens.kmp.compose.interpolated.isdpLw
import com.appdimens.kmp.compose.interpolated.isdpMode
import com.appdimens.kmp.compose.interpolated.isdpPh
import com.appdimens.kmp.compose.interpolated.isdpQualifier
import com.appdimens.kmp.compose.interpolated.isdpRotate
import com.appdimens.kmp.compose.interpolated.isdpRotatePlain
import com.appdimens.kmp.compose.interpolated.isdpScreen
import com.appdimens.kmp.compose.interpolated.isdpModePlain
import com.appdimens.kmp.compose.interpolated.isdpQualifierPlain
import com.appdimens.kmp.compose.interpolated.isdpScreenPlain
import com.appdimens.kmp.compose.interpolated.iwdp
import com.appdimens.kmp.compose.interpolated.iwdpLh
import com.appdimens.kmp.compose.logarithmic.logarithmicDp
import com.appdimens.kmp.compose.logarithmic.logsdpa
import com.appdimens.kmp.compose.logarithmic.loghdp
import com.appdimens.kmp.compose.logarithmic.loghdpLw
import com.appdimens.kmp.compose.logarithmic.logsdp
import com.appdimens.kmp.compose.logarithmic.logsdpLh
import com.appdimens.kmp.compose.logarithmic.logsdpLw
import com.appdimens.kmp.compose.logarithmic.logsdpMode
import com.appdimens.kmp.compose.logarithmic.logsdpPh
import com.appdimens.kmp.compose.logarithmic.logsdpQualifier
import com.appdimens.kmp.compose.logarithmic.logsdpRotate
import com.appdimens.kmp.compose.logarithmic.logsdpRotatePlain
import com.appdimens.kmp.compose.logarithmic.logsdpScreen
import com.appdimens.kmp.compose.logarithmic.logsdpModePlain
import com.appdimens.kmp.compose.logarithmic.logsdpQualifierPlain
import com.appdimens.kmp.compose.logarithmic.logsdpScreenPlain
import com.appdimens.kmp.compose.logarithmic.logwdp
import com.appdimens.kmp.compose.logarithmic.logwdpLh
import com.appdimens.kmp.compose.percent.percentDp
import com.appdimens.kmp.compose.percent.psdpa
import com.appdimens.kmp.compose.percent.phdp
import com.appdimens.kmp.compose.percent.phdpLw
import com.appdimens.kmp.compose.percent.psdp
import com.appdimens.kmp.compose.percent.psdpLh
import com.appdimens.kmp.compose.percent.psdpLw
import com.appdimens.kmp.compose.percent.psdpMode
import com.appdimens.kmp.compose.percent.psdpPh
import com.appdimens.kmp.compose.percent.psdpQualifier
import com.appdimens.kmp.compose.percent.psdpRotate
import com.appdimens.kmp.compose.percent.psdpRotatePlain
import com.appdimens.kmp.compose.percent.psdpScreen
import com.appdimens.kmp.compose.percent.psdpModePlain
import com.appdimens.kmp.compose.percent.psdpQualifierPlain
import com.appdimens.kmp.compose.percent.psdpScreenPlain
import com.appdimens.kmp.compose.percent.pwdp
import com.appdimens.kmp.compose.percent.pwdpLh
import com.appdimens.kmp.compose.perimeter.perimeterDp
import com.appdimens.kmp.compose.perimeter.prsdpa
import com.appdimens.kmp.compose.perimeter.prhdp
import com.appdimens.kmp.compose.perimeter.prhdpLw
import com.appdimens.kmp.compose.perimeter.prsdp
import com.appdimens.kmp.compose.perimeter.prsdpLh
import com.appdimens.kmp.compose.perimeter.prsdpLw
import com.appdimens.kmp.compose.perimeter.prsdpMode
import com.appdimens.kmp.compose.perimeter.prsdpPh
import com.appdimens.kmp.compose.perimeter.prsdpQualifier
import com.appdimens.kmp.compose.perimeter.prsdpRotate
import com.appdimens.kmp.compose.perimeter.prsdpRotatePlain
import com.appdimens.kmp.compose.perimeter.prsdpScreen
import com.appdimens.kmp.compose.perimeter.prsdpModePlain
import com.appdimens.kmp.compose.perimeter.prsdpQualifierPlain
import com.appdimens.kmp.compose.perimeter.prsdpScreenPlain
import com.appdimens.kmp.compose.perimeter.prwdp
import com.appdimens.kmp.compose.perimeter.prwdpLh
import com.appdimens.kmp.compose.power.powerDp
import com.appdimens.kmp.compose.power.pwsdpa
import com.appdimens.kmp.compose.power.pwhdp
import com.appdimens.kmp.compose.power.pwhdpLw
import com.appdimens.kmp.compose.power.pwsdp
import com.appdimens.kmp.compose.power.pwsdpLh
import com.appdimens.kmp.compose.power.pwsdpLw
import com.appdimens.kmp.compose.power.pwsdpMode
import com.appdimens.kmp.compose.power.pwsdpPh
import com.appdimens.kmp.compose.power.pwsdpQualifier
import com.appdimens.kmp.compose.power.pwsdpRotate
import com.appdimens.kmp.compose.power.pwsdpRotatePlain
import com.appdimens.kmp.compose.power.pwsdpScreen
import com.appdimens.kmp.compose.power.pwsdpModePlain
import com.appdimens.kmp.compose.power.pwsdpQualifierPlain
import com.appdimens.kmp.compose.power.pwsdpScreenPlain
import com.appdimens.kmp.compose.power.pwwdp
import com.appdimens.kmp.compose.power.pwwdpLh
import com.appdimens.kmp.compose.auto.assp
import com.appdimens.kmp.compose.auto.asspRotatePlain as stratSspRotatePlainAuto
import com.appdimens.kmp.compose.density.dssp
import com.appdimens.kmp.compose.density.dsspRotatePlain as stratSspRotatePlainDensity
import com.appdimens.kmp.compose.diagonal.dgssp
import com.appdimens.kmp.compose.diagonal.dgsspRotatePlain as stratSspRotatePlainDiagonal
import com.appdimens.kmp.compose.fill.flssp
import com.appdimens.kmp.compose.fill.flsspRotatePlain as stratSspRotatePlainFill
import com.appdimens.kmp.compose.fit.ftssp
import com.appdimens.kmp.compose.fit.ftsspRotatePlain as stratSspRotatePlainFit
import com.appdimens.kmp.compose.fluid.fssp
import com.appdimens.kmp.compose.fluid.fsspRotatePlain as stratSspRotatePlainFluid
import com.appdimens.kmp.compose.interpolated.issp
import com.appdimens.kmp.compose.interpolated.isspRotatePlain as stratSspRotatePlainInterpolated
import com.appdimens.kmp.compose.logarithmic.logssp
import com.appdimens.kmp.compose.logarithmic.logsspRotatePlain as stratSspRotatePlainLogarithmic
import com.appdimens.kmp.compose.percent.pssp
import com.appdimens.kmp.compose.percent.psspRotatePlain as stratSspRotatePlainPercent
import com.appdimens.kmp.compose.perimeter.prssp
import com.appdimens.kmp.compose.perimeter.prsspRotatePlain as stratSspRotatePlainPerimeter
import com.appdimens.kmp.compose.power.pwssp
import com.appdimens.kmp.compose.power.pwsspRotatePlain as stratSspRotatePlainPower
import com.appdimens.kmp.compose.ssp
import com.appdimens.kmp.compose.sspRotatePlain as stratSspRotatePlainScaled
import com.appdimens.kmp.compose.sdp
import com.appdimens.kmp.compose.sdpRotatePlain
import com.appdimens.kmp.compose.sdpModePlain
import com.appdimens.kmp.compose.sdpQualifierPlain
import com.appdimens.kmp.compose.sdpScreenPlain
import com.appdimens.kmp.compose.sdpa
import com.appdimens.kmp.compose.hdp
import com.appdimens.kmp.compose.hdpLw
import com.appdimens.kmp.compose.scaledDp
import com.appdimens.kmp.compose.sdpLh
import com.appdimens.kmp.compose.sdpLw
import com.appdimens.kmp.compose.sdpMode
import com.appdimens.kmp.compose.sdpPh
import com.appdimens.kmp.compose.sdpQualifier
import com.appdimens.kmp.compose.sdpRotate
import com.appdimens.kmp.compose.sdpScreen
import com.appdimens.kmp.compose.wdp
import com.appdimens.kmp.compose.wdpLh

enum class DemoCalcStrategy(val labelEn: String, val labelPt: String) {
    Scaled("Scaled (default)", "Scaled (padrão)"),
    Percent("Percent", "Percent"),
    Power("Power", "Power"),
    Auto("Auto", "Auto"),
    Logarithmic("Logarithmic", "Logarítmico"),
    Fluid("Fluid", "Fluid"),
    Interpolated("Interpolated", "Interpolado"),
    Diagonal("Diagonal", "Diagonal"),
    Perimeter("Perimeter", "Perímetro"),
    Fit("Fit", "Fit"),
    Fill("Fill", "Fill"),
    Density("Density", "Densidade"),
}

val LocalDemoCalcStrategy = compositionLocalOf { DemoCalcStrategy.Scaled }

@get:Composable
val Number.demoSwDp: Dp
    get() =
        when (LocalDemoCalcStrategy.current) {
            DemoCalcStrategy.Scaled -> this.sdp
            DemoCalcStrategy.Percent -> this.psdp
            DemoCalcStrategy.Power -> this.pwsdp
            DemoCalcStrategy.Auto -> this.asdp
            DemoCalcStrategy.Logarithmic -> this.logsdp
            DemoCalcStrategy.Fluid -> this.fsdp
            DemoCalcStrategy.Interpolated -> this.isdp
            DemoCalcStrategy.Diagonal -> this.dgsdp
            DemoCalcStrategy.Perimeter -> this.prsdp
            DemoCalcStrategy.Fit -> this.ftsdp
            DemoCalcStrategy.Fill -> this.flsdp
            DemoCalcStrategy.Density -> this.dsdp
        }

@get:Composable
val Number.demoSwDpa: Dp
    get() =
        when (LocalDemoCalcStrategy.current) {
            DemoCalcStrategy.Scaled -> this.sdpa
            DemoCalcStrategy.Percent -> this.psdpa
            DemoCalcStrategy.Power -> this.pwsdpa
            DemoCalcStrategy.Auto -> this.asdpa
            DemoCalcStrategy.Logarithmic -> this.logsdpa
            DemoCalcStrategy.Fluid -> this.fsdpa
            DemoCalcStrategy.Interpolated -> this.isdpa
            DemoCalcStrategy.Diagonal -> this.dgsdpa
            DemoCalcStrategy.Perimeter -> this.prsdpa
            DemoCalcStrategy.Fit -> this.ftsdpa
            DemoCalcStrategy.Fill -> this.flsdpa
            DemoCalcStrategy.Density -> this.dsdpa
        }

@get:Composable
val Number.demoHdp: Dp
    get() =
        when (LocalDemoCalcStrategy.current) {
            DemoCalcStrategy.Scaled -> this.hdp
            DemoCalcStrategy.Percent -> this.phdp
            DemoCalcStrategy.Power -> this.pwhdp
            DemoCalcStrategy.Auto -> this.ahdp
            DemoCalcStrategy.Logarithmic -> this.loghdp
            DemoCalcStrategy.Fluid -> this.fhdp
            DemoCalcStrategy.Interpolated -> this.ihdp
            DemoCalcStrategy.Diagonal -> this.dghdp
            DemoCalcStrategy.Perimeter -> this.prhdp
            DemoCalcStrategy.Fit -> this.fthdp
            DemoCalcStrategy.Fill -> this.flhdp
            DemoCalcStrategy.Density -> this.dhdp
        }

@get:Composable
val Number.demoWdp: Dp
    get() =
        when (LocalDemoCalcStrategy.current) {
            DemoCalcStrategy.Scaled -> this.wdp
            DemoCalcStrategy.Percent -> this.pwdp
            DemoCalcStrategy.Power -> this.pwwdp
            DemoCalcStrategy.Auto -> this.awdp
            DemoCalcStrategy.Logarithmic -> this.logwdp
            DemoCalcStrategy.Fluid -> this.fwdp
            DemoCalcStrategy.Interpolated -> this.iwdp
            DemoCalcStrategy.Diagonal -> this.dgwdp
            DemoCalcStrategy.Perimeter -> this.prwdp
            DemoCalcStrategy.Fit -> this.ftwdp
            DemoCalcStrategy.Fill -> this.flwdp
            DemoCalcStrategy.Density -> this.dwdp
        }

@get:Composable
val Number.demoSwPh: Dp
    get() =
        when (LocalDemoCalcStrategy.current) {
            DemoCalcStrategy.Scaled -> this.sdpPh
            DemoCalcStrategy.Percent -> this.psdpPh
            DemoCalcStrategy.Power -> this.pwsdpPh
            DemoCalcStrategy.Auto -> this.asdpPh
            DemoCalcStrategy.Logarithmic -> this.logsdpPh
            DemoCalcStrategy.Fluid -> this.fsdpPh
            DemoCalcStrategy.Interpolated -> this.isdpPh
            DemoCalcStrategy.Diagonal -> this.dgsdpPh
            DemoCalcStrategy.Perimeter -> this.prsdpPh
            DemoCalcStrategy.Fit -> this.ftsdpPh
            DemoCalcStrategy.Fill -> this.flsdpPh
            DemoCalcStrategy.Density -> this.dsdpPh
        }

@get:Composable
val Number.demoSwLw: Dp
    get() =
        when (LocalDemoCalcStrategy.current) {
            DemoCalcStrategy.Scaled -> this.sdpLw
            DemoCalcStrategy.Percent -> this.psdpLw
            DemoCalcStrategy.Power -> this.pwsdpLw
            DemoCalcStrategy.Auto -> this.asdpLw
            DemoCalcStrategy.Logarithmic -> this.logsdpLw
            DemoCalcStrategy.Fluid -> this.fsdpLw
            DemoCalcStrategy.Interpolated -> this.isdpLw
            DemoCalcStrategy.Diagonal -> this.dgsdpLw
            DemoCalcStrategy.Perimeter -> this.prsdpLw
            DemoCalcStrategy.Fit -> this.ftsdpLw
            DemoCalcStrategy.Fill -> this.flsdpLw
            DemoCalcStrategy.Density -> this.dsdpLw
        }

@get:Composable
val Number.demoHLw: Dp
    get() =
        when (LocalDemoCalcStrategy.current) {
            DemoCalcStrategy.Scaled -> this.hdpLw
            DemoCalcStrategy.Percent -> this.phdpLw
            DemoCalcStrategy.Power -> this.pwhdpLw
            DemoCalcStrategy.Auto -> this.ahdpLw
            DemoCalcStrategy.Logarithmic -> this.loghdpLw
            DemoCalcStrategy.Fluid -> this.fhdpLw
            DemoCalcStrategy.Interpolated -> this.ihdpLw
            DemoCalcStrategy.Diagonal -> this.dghdpLw
            DemoCalcStrategy.Perimeter -> this.prhdpLw
            DemoCalcStrategy.Fit -> this.fthdpLw
            DemoCalcStrategy.Fill -> this.flhdpLw
            DemoCalcStrategy.Density -> this.dhdpLw
        }

@get:Composable
val Number.demoWLh: Dp
    get() =
        when (LocalDemoCalcStrategy.current) {
            DemoCalcStrategy.Scaled -> this.wdpLh
            DemoCalcStrategy.Percent -> this.pwdpLh
            DemoCalcStrategy.Power -> this.pwwdpLh
            DemoCalcStrategy.Auto -> this.awdpLh
            DemoCalcStrategy.Logarithmic -> this.logwdpLh
            DemoCalcStrategy.Fluid -> this.fwdpLh
            DemoCalcStrategy.Interpolated -> this.iwdpLh
            DemoCalcStrategy.Diagonal -> this.dgwdpLh
            DemoCalcStrategy.Perimeter -> this.prwdpLh
            DemoCalcStrategy.Fit -> this.ftwdpLh
            DemoCalcStrategy.Fill -> this.flwdpLh
            DemoCalcStrategy.Density -> this.dwdpLh
        }

@get:Composable
val Number.demoSwLh: Dp
    get() =
        when (LocalDemoCalcStrategy.current) {
            DemoCalcStrategy.Scaled -> this.sdpLh
            DemoCalcStrategy.Percent -> this.psdpLh
            DemoCalcStrategy.Power -> this.pwsdpLh
            DemoCalcStrategy.Auto -> this.asdpLh
            DemoCalcStrategy.Logarithmic -> this.logsdpLh
            DemoCalcStrategy.Fluid -> this.fsdpLh
            DemoCalcStrategy.Interpolated -> this.isdpLh
            DemoCalcStrategy.Diagonal -> this.dgsdpLh
            DemoCalcStrategy.Perimeter -> this.prsdpLh
            DemoCalcStrategy.Fit -> this.ftsdpLh
            DemoCalcStrategy.Fill -> this.flsdpLh
            DemoCalcStrategy.Density -> this.dsdpLh
        }

@Composable
fun Int.demoSdpRotate(
    rotationValue: Number,
    finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH,
    orientation: Orientation = Orientation.LANDSCAPE,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null,
): Dp =
    when (LocalDemoCalcStrategy.current) {
        DemoCalcStrategy.Scaled ->
            this.sdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Percent ->
            this.psdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Power ->
            this.pwsdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Auto ->
            this.asdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Logarithmic ->
            this.logsdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fluid ->
            this.fsdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Interpolated ->
            this.isdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Diagonal ->
            this.dgsdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Perimeter ->
            this.prsdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fit ->
            this.ftsdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fill ->
            this.flsdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Density ->
            this.dsdpRotate(rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
    }

@Composable
fun Int.demoSdpMode(
    modeValue: Number,
    uiModeType: UiModeType,
    finalQualifierResolver: DpQualifier? = null,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null,
): Dp =
    when (LocalDemoCalcStrategy.current) {
        DemoCalcStrategy.Scaled -> this.sdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Percent -> this.psdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Power -> this.pwsdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Auto -> this.asdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Logarithmic -> this.logsdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fluid -> this.fsdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Interpolated -> this.isdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Diagonal -> this.dgsdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Perimeter -> this.prsdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fit -> this.ftsdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fill -> this.flsdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Density -> this.dsdpMode(modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
    }

@Composable
fun Number.demoSdpQualifier(
    qualifiedValue: Number,
    qualifierType: DpQualifier,
    qualifierValue: Number,
    finalQualifierResolver: DpQualifier? = null,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null,
): Dp =
    when (LocalDemoCalcStrategy.current) {
        DemoCalcStrategy.Scaled -> this.sdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Percent -> this.psdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Power -> this.pwsdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Auto -> this.asdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Logarithmic -> this.logsdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fluid -> this.fsdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Interpolated -> this.isdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Diagonal -> this.dgsdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Perimeter -> this.prsdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fit -> this.ftsdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fill -> this.flsdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Density -> this.dsdpQualifier(qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
    }

@Composable
fun Number.demoSdpScreen(
    screenValue: Number,
    uiModeType: UiModeType,
    qualifierType: DpQualifier,
    qualifierValue: Number,
    finalQualifierResolver: DpQualifier? = null,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null,
): Dp =
    when (LocalDemoCalcStrategy.current) {
        DemoCalcStrategy.Scaled -> this.sdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Percent -> this.psdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Power -> this.pwsdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Auto -> this.asdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Logarithmic -> this.logsdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fluid -> this.fsdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Interpolated -> this.isdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Diagonal -> this.dgsdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Perimeter -> this.prsdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fit -> this.ftsdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Fill -> this.flsdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        DemoCalcStrategy.Density -> this.dsdpScreen(screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
    }

@Composable
fun Dp.demoSdpRotatePlain(rotation: Dp, orientation: Orientation = Orientation.LANDSCAPE): Dp =
    when (LocalDemoCalcStrategy.current) {
        DemoCalcStrategy.Scaled -> this.sdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Percent -> this.psdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Power -> this.pwsdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Auto -> this.asdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Logarithmic -> this.logsdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Fluid -> this.fsdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Interpolated -> this.isdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Diagonal -> this.dgsdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Perimeter -> this.prsdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Fit -> this.ftsdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Fill -> this.flsdpRotatePlain(rotation, orientation)
        DemoCalcStrategy.Density -> this.dsdpRotatePlain(rotation, orientation)
    }

@Composable
fun Dp.demoSdpModePlain(mode: Dp, uiModeType: UiModeType): Dp =
    when (LocalDemoCalcStrategy.current) {
        DemoCalcStrategy.Scaled -> this.sdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Percent -> this.psdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Power -> this.pwsdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Auto -> this.asdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Logarithmic -> this.logsdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Fluid -> this.fsdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Interpolated -> this.isdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Diagonal -> this.dgsdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Perimeter -> this.prsdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Fit -> this.ftsdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Fill -> this.flsdpModePlain(mode, uiModeType)
        DemoCalcStrategy.Density -> this.dsdpModePlain(mode, uiModeType)
    }

@Composable
fun Dp.demoSdpQualifierPlain(qualified: Dp, qualifierType: DpQualifier, qualifierValue: Number): Dp =
    when (LocalDemoCalcStrategy.current) {
        DemoCalcStrategy.Scaled -> this.sdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Percent -> this.psdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Power -> this.pwsdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Auto -> this.asdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Logarithmic -> this.logsdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Fluid -> this.fsdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Interpolated -> this.isdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Diagonal -> this.dgsdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Perimeter -> this.prsdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Fit -> this.ftsdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Fill -> this.flsdpQualifierPlain(qualified, qualifierType, qualifierValue)
        DemoCalcStrategy.Density -> this.dsdpQualifierPlain(qualified, qualifierType, qualifierValue)
    }

@Composable
fun Dp.demoSdpScreenPlain(screen: Dp, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Dp =
    when (LocalDemoCalcStrategy.current) {
        DemoCalcStrategy.Scaled -> this.sdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Percent -> this.psdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Power -> this.pwsdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Auto -> this.asdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Logarithmic -> this.logsdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Fluid -> this.fsdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Interpolated -> this.isdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Diagonal -> this.dgsdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Perimeter -> this.prsdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Fit -> this.ftsdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Fill -> this.flsdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
        DemoCalcStrategy.Density -> this.dsdpScreenPlain(screen, uiModeType, qualifierType, qualifierValue)
    }

@get:Composable
val Number.demoSsp: TextUnit
    get() =
        when (LocalDemoCalcStrategy.current) {
            DemoCalcStrategy.Scaled -> this.ssp
            DemoCalcStrategy.Percent -> this.pssp
            DemoCalcStrategy.Power -> this.pwssp
            DemoCalcStrategy.Auto -> this.assp
            DemoCalcStrategy.Logarithmic -> this.logssp
            DemoCalcStrategy.Fluid -> this.fssp
            DemoCalcStrategy.Interpolated -> this.issp
            DemoCalcStrategy.Diagonal -> this.dgssp
            DemoCalcStrategy.Perimeter -> this.prssp
            DemoCalcStrategy.Fit -> this.ftssp
            DemoCalcStrategy.Fill -> this.flssp
            DemoCalcStrategy.Density -> this.dssp
        }

@Composable
fun TextUnit.demoSspRotatePlain(rotation: TextUnit, orientation: Orientation = Orientation.LANDSCAPE): TextUnit =
    when (LocalDemoCalcStrategy.current) {
        DemoCalcStrategy.Scaled -> this.stratSspRotatePlainScaled(rotation, orientation)
        DemoCalcStrategy.Percent -> this.stratSspRotatePlainPercent(rotation, orientation)
        DemoCalcStrategy.Power -> this.stratSspRotatePlainPower(rotation, orientation)
        DemoCalcStrategy.Auto -> this.stratSspRotatePlainAuto(rotation, orientation)
        DemoCalcStrategy.Logarithmic -> this.stratSspRotatePlainLogarithmic(rotation, orientation)
        DemoCalcStrategy.Fluid -> this.stratSspRotatePlainFluid(rotation, orientation)
        DemoCalcStrategy.Interpolated -> this.stratSspRotatePlainInterpolated(rotation, orientation)
        DemoCalcStrategy.Diagonal -> this.stratSspRotatePlainDiagonal(rotation, orientation)
        DemoCalcStrategy.Perimeter -> this.stratSspRotatePlainPerimeter(rotation, orientation)
        DemoCalcStrategy.Fit -> this.stratSspRotatePlainFit(rotation, orientation)
        DemoCalcStrategy.Fill -> this.stratSspRotatePlainFill(rotation, orientation)
        DemoCalcStrategy.Density -> this.stratSspRotatePlainDensity(rotation, orientation)
    }

@Composable
fun demoDimenScaledResultDp(): Dp {
    val mode = LocalDemoCalcStrategy.current
    return when (mode) {
        DemoCalcStrategy.Scaled ->
            100.scaledDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .sdp
        DemoCalcStrategy.Percent ->
            100.percentDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .psdp
        DemoCalcStrategy.Power ->
            100.powerDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .pwsdp
        DemoCalcStrategy.Auto ->
            100.autoDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .asdp
        DemoCalcStrategy.Logarithmic ->
            100.logarithmicDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .logsdp
        DemoCalcStrategy.Fluid ->
            100.fluidDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .fsdp
        DemoCalcStrategy.Interpolated ->
            100.interpolatedDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .isdp
        DemoCalcStrategy.Diagonal ->
            100.diagonalDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .dgsdp
        DemoCalcStrategy.Perimeter ->
            100.perimeterDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .prsdp
        DemoCalcStrategy.Fit ->
            100.fitDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .ftsdp
        DemoCalcStrategy.Fill ->
            100.fillDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .flsdp
        DemoCalcStrategy.Density ->
            100.densityDp()
                .screen(UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600, 250)
                .screen(UiModeType.TELEVISION, 500)
                .screen(UiModeType.FOLD_OPEN, 200)
                .screen(DpQualifier.SMALL_WIDTH, 600, 150)
                .screen(Orientation.LANDSCAPE, 120)
                .dsdp
    }
}
