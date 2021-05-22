![Build Status](https://github.com/dkandalov/datsok/workflows/CI/badge.svg)

This is a minimal library with assertions for writing tests in Kotlin.
It has no dependencies except Kotlin stdlib and has only three functions `shouldEqual`, `shouldNotEqual` and `shouldThrow`.

### Examples
```kotlin
123 shouldEqual 123
123 shouldNotEqual 42

"foo" shouldEqual "foo"
"foo" shouldNotEqual "bar"

arrayOf(1, 2, 3) shouldEqual arrayOf(1, 2, 3)
(1 + 2 shouldEqual 3) * 5 shouldEqual 15

shouldThrow<Exception> { throw IllegalStateException() }
shouldThrow(SomeException("a message")) { throw SomeException("a message") }
```

### Adding Gradle dependency
The library is only published on GitHub, so you'll need the following to add dependency in Gradle:
```groovy
repositories {
    mavenCentral()
    ivy {
        artifactPattern("https://raw.githubusercontent.com/dkandalov/datsok/master/jars/[artifact]-[revision](.[ext])")
    }
}
dependencies {
    // ...
    testImplementation("datsok:datsok:0.5")
}
```