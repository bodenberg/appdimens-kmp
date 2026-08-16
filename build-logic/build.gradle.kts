plugins {
    `kotlin-dsl`
}

// EN The convention plugin applies these plugins from its own `plugins {}` block,
//    so their implementation classes must be on the precompiled-script classpath.
//    Use the real Maven coordinates (a bare `pluginId:version` is not resolvable
//    as a module dependency).
// PT O convention plugin aplica estes plugins do seu próprio bloco `plugins {}`,
//    então as classes de implementação precisam estar no classpath do script
//    pré-compilado. Usamos as coordenadas Maven reais.
dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    compileOnly("org.jetbrains.compose:compose-gradle-plugin:${libs.versions.composeMultiplatform.get()}")
    compileOnly("org.jetbrains.kotlin:compose-compiler-gradle-plugin:${libs.versions.kotlin.get()}")
    compileOnly("com.android.tools.build:gradle:${libs.versions.agp.get()}")
    compileOnly("com.vanniktech:gradle-maven-publish-plugin:${libs.versions.vanniktech.get()}")
}
