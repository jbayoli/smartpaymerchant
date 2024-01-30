pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        flatDir {
            dir("libs")
        }

        google()
        maven(url = "https://repo.visa.com/mpos-releases/")
        mavenCentral()

    }
}

rootProject.name = "SmartPay Marchant"
include(":app")