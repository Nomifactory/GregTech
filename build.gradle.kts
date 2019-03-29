import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayExtension.PackageConfig
import com.jfrog.bintray.gradle.BintrayExtension.VersionConfig
import com.matthewprenger.cursegradle.CurseExtension
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import net.minecraftforge.gradle.user.UserBaseExtension
import org.ajoberstar.grgit.Grgit
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

buildscript {
    repositories {
        jcenter()
        maven {
            name = "forge"
            setUrl("http://files.minecraftforge.net/maven")
        }
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT")
    }
}

plugins {
    id("com.matthewprenger.cursegradle") version "1.1.0"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4"
    id("org.ajoberstar.grgit") version "2.2.0"
}

apply {
    plugin("net.minecraftforge.gradle.forge")
}

val config: Properties = file("build.properties").inputStream().let {
    val prop = Properties()
    prop.load(it)
    return@let prop
}

val mcVersion = config["minecraft.version"] as String
val mcFullVersion = "$mcVersion-${config["forge.version"]}"
val shortVersion = mcVersion.substring(0, mcVersion.lastIndexOf("."))
val strippedVersion = shortVersion.replace(".", "") + "0"

val forestryVersion = config["forestry.version"] as String
val chickenasmVersion = config["chickenasm.version"] as String
val cclVersion = config["ccl.version"] as String
val multipartVersion = config["multipart.version"] as String
val crafttweakerVersion = config["crafttweaker.version"] as String
val jeiVersion = config["jei.version"] as String
val topVersion = config["top.version"] as String
val thermaldynamicsVersion = config["thermaldynamics.version"] as String

val modVersion = getVersionFromJava(file("src/main/java/gregtech/GregTechVersion.java"))
version = "$mcVersion-$modVersion"
group = "gregtech"

configure<BasePluginConvention> {
    archivesBaseName = "gregtech"
}

fun minecraft(configure: UserBaseExtension.() -> Unit) = project.configure(configure)
minecraft {
    version = mcFullVersion
	mappings = "snapshot_20170928"
    runDir = "run"
    isUseDepAts = true
}

configurations {
    val provided by configurations.getting

    "compile" {
        extendsFrom(provided)
    }
}

repositories {
    maven {
        name = "ic2, forestry"
        setUrl("http://maven.ic2.player.to/")
    }
    maven { //JEI
        name = "Progwml6 maven"
        setUrl("http://dvs1.progwml6.com/files/maven/")
    }
    maven { //JEI fallback
        name = "ModMaven"
        setUrl("modmaven.k-4u.nl")
    }
    maven {
        name = "tterrag maven"
        setUrl("http://maven.tterrag.com/")
    }
    maven {
        name = "ChickenBones maven"
        setUrl("http://chickenbones.net/maven/")
    }
    maven {
        name = "CoFH Maven"
        setUrl("http://maven.covers1624.net")
    }
    maven {
        name = "tehnut maven"
        setUrl("http://tehnut.info/maven/")
    }
    maven {
        name = "CraftTweaker Maven"
        setUrl("https://maven.blamejared.com/")
    }
}

dependencies {
    "deobfCompile"("net.sengir.forestry:forestry_$mcVersion:$forestryVersion") {
        isTransitive = false
    }

    "deobfCompile"("codechicken:ChickenASM:$shortVersion-$chickenasmVersion")
    "deobfCompile"("codechicken:CodeChickenLib:$mcVersion-$cclVersion:deobf")
    "deobfCompile"("codechicken:ForgeMultipart:$mcVersion-$multipartVersion:deobf")
    "deobfCompile"("CraftTweaker2:CraftTweaker2-MC$strippedVersion-Main:$crafttweakerVersion")
    "deobfCompile"("mezz.jei:jei_$mcVersion:$jeiVersion:api")
    "deobfCompile"("mcjty.theoneprobe:TheOneProbe-$shortVersion:$shortVersion-$topVersion")
    "deobfCompile"("cofh:ThermalDynamics:$mcVersion-$thermaldynamicsVersion:deobf")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val processResources: ProcessResources by tasks
val sourceSets: SourceSetContainer = the<JavaPluginConvention>().sourceSets

processResources.apply {
    // this will ensure that this task is redone when the versions change.
    inputs.property("version", modVersion)
    inputs.property("mcversion", mcFullVersion)

    // replace stuff in mcmod.info, nothing else
    from(sourceSets["main"].resources.srcDirs) {
        include("mcmod.info")

        // replace version and mcversion
        expand(mapOf("version" to modVersion, "mcversion" to mcFullVersion))
    }

    // copy everything else, thats not the mcmod.info
    from(sourceSets["main"].resources.srcDirs) {
        exclude("mcmod.info")
    }

	// access transformer
	rename("(.+_at.cfg)", "META-INF/$1")
}

val jar: Jar by tasks
jar.apply {
	manifest {
	    attributes(mapOf("FMLAT" to "gregtech_at.cfg"))
	}
}

val sourceTask: Jar = tasks.create("source", Jar::class.java) {
    from(sourceSets["main"].allSource)
    classifier = "sources"
}

val devTask: Jar = tasks.create("dev", Jar::class.java) {
    from(sourceSets["main"].output)
    classifier = "dev"
}

val energyApiTask: Jar = tasks.create("energyApi", Jar::class.java) {
    from(sourceSets["main"].allSource)
    from(sourceSets["main"].output)

    include("gregtech/api/capability/IElectricItem.*")
    include("gregtech/api/capability/IEnergyContainer.*")
    include("gregtech/api/capability/GregtechCapabilities.*")

    classifier = "energy-api"
}

artifacts {
    add("archives", jar)
    add("archives", sourceTask)
    add("archives", energyApiTask)
}

fun org.gradle.api.Project.idea(configure: org.gradle.plugins.ide.idea.model.IdeaModel.() -> Unit): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("idea", configure)
idea {
    module {
        inheritOutputDirs = true
    }
}

tasks.create("ciWriteBuildNumber") {
    doLast {
        val file = file("src/main/java/gregtech/GregTechVersion.java")
        val bn = getBuildNumber()
        val ln = "\n" //Linux line endings because we"re on git!

        var outfile = ""

        println("Build number: $bn")

        file.forEachLine { s ->
            var out = s
            if (out.matches(Regex("^ {4}public static final int BUILD = [\\d]+;\$"))) {
                out = "    public static final int BUILD = $bn;"
            }
            outfile += (out + ln)
        }
        file.writeText(outfile)
    }
}

fun getBuildNumber(): String {
    val grgit = Grgit.open(mapOf("dir" to "."))
    val commits = grgit.log {
        it.range("c795901d796fba8ce8d3cb87d0172c59f56f3c9b", "HEAD")
    }

    return "${commits.size}"
}

fun getVersionFromJava(file: File): String  {
    var major = "0"
    var minor = "0"
    var revision = "0"

    val prefix = "public static final int"
    file.forEachLine { line ->
        var s = line.trim()
        if (s.startsWith(prefix)) {
            s = s.substring(prefix.length, s.length - 1)
            s = s.replace("=", " ").replace(" +", " ").trim()
            val pts = s.split(" ")

            when {
                pts[0] == "MAJOR" -> major = pts[pts.size - 1]
                pts[0] == "MINOR" -> minor = pts[pts.size - 1]
                pts[0] == "REVISION" -> revision = pts[pts.size - 1]
            }
        }
    }

    val branchNameOrTag = System.getenv("CI_COMMIT_REF_NAME")
    if (branchNameOrTag != null && !branchNameOrTag.startsWith("v") && branchNameOrTag != "master") {
        return "$major.$minor.$revision-$branchNameOrTag"
    }

    val build = getBuildNumber()

    return "$major.$minor.$revision.$build"
}

fun CurseExtension.project(config: CurseProject.() -> Unit) = CurseProject().also {
    it.config()
    curseProjects.add(it)
}


// has to be called after addArtifact ¯\_(ツ)_/¯
fun CurseProject.relations(config: CurseRelation.() -> Unit) = CurseRelation().also {

    it.config()

    additionalArtifacts.forEach { artifact ->
        artifact.curseRelations = it
    }

    mainArtifact.curseRelations = it
}

curseforge {
    if (System.getenv("CURSE_API_KEY") == null) {
        println("Skipping curseforge task as there is no api key in the environment")
        return@curseforge
    }

    apiKey = System.getenv("CURSE_API_KEY")

    project {
        id = "293327"
        changelog = file("CHANGELOG.md")
        changelogType = "markdown"
        releaseType = "beta"

        addArtifact(sourceTask)
        addArtifact(energyApiTask)

        relations {
            requiredDependency("codechicken-lib-1-8")
            optionalDependency("forge-multipart-cbe")
            optionalDependency("crafttweaker")
            optionalDependency("jei")
            optionalDependency("the-one-probe")
        }
    }
}

publishing {
    publications {
        create("GTCEPublication", MavenPublication::class.java) {
            groupId = project.group as String
            artifactId = the<BasePluginConvention>().archivesBaseName
            version = project.version as String

            artifact(jar)
            artifact(sourceTask)
            artifact(energyApiTask)
        }
    }
}


fun BintrayExtension.pkg(config: PackageConfig.() -> Unit) = PackageConfig().also {
    it.config()
    this.pkg = it
}

fun BintrayExtension.version(config: VersionConfig.() -> Unit) {
    VersionConfig().also {
        it.config()
        this.pkg.version = it
    }
}

bintray {
    val bintrayUser = if (project.hasProperty("bintrayUser")) project.property("bintrayUser") as String else System.getenv("BINTRAY_USER")
    val bintrayApiKey = if (project.hasProperty("bintrayApiKey")) project.property("bintrayApiKey") as String else System.getenv("BINTRAY_API_KEY")

    if (bintrayUser == null || bintrayApiKey == null) {
        println("Skipping bintrayUpload task as there is no api key or user in the environment")
        return@bintray
    }

    user = bintrayUser
    key = bintrayApiKey
    setPublications("GTCEPublication")
    publish = true
    pkg {
        repo = "dev"
        name = "GregTechCE"
        userOrg = "gregtech"
        setLicenses("LGPL-3.0")
        vcsUrl = "https://github.com/GregTechCE/GregTech.git"
        version {
            name = project.version as String
            released = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
        }
    }
}