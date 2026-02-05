# Jod - A Zod-like Validation Library for Java

![Java 21](https://img.shields.io/badge/Java-21-orange?logo=java)
![License](https://img.shields.io/badge/License-MIT-blue)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)

**Jod** is a comprehensive validation library for Java that provides type-safe schema validation with a fluent API, inspired by [Zod](https://zod.dev) from the TypeScript ecosystem.

## Features

- **Type-safe validation** using Java 21 features and Method References
- **Fluent API** for building complex validation schemas naturally
- **Primitive types**: String, Integer, Long, Double, Boolean, Date validation
- **Complex types**: Object, List, Map validation
- **Advanced features**: Union types, Enums, Literals, Recursive schemas
- **Flexible constraints**: Min/Max, Regex, Email, UUID, URL validation
- **Transformations**: String trimming, case conversion, parsing
- **Optional and default values** support
- **Nested validation** with deep error paths (e.g., `users[0].address.city`)
- **Lazy evaluation** for self-referencing (recursive) schemas

## Installation

### Maven
```xml
<dependency>
    <groupId>dev.anchxt</groupId>
    <artifactId>jod</artifactId>
    <version>1.0</version>
</dependency>
```

### Gradle
```Kotlin

implementation("dev.anchxt:jod:1.0")
```
## Quick Start
```Java

package com.example;

import dev.anchxt.jod.Jod;
import dev.anchxt.jod.ValidationResult;

public class Main {
// Define your data model
record User(String name, String email, int age) {}

    public static void main(String[] args) {
        // 1. Define a user schema
        var userSchema = Jod.object(User.class)
            .field(User::name, Jod.string().min(2).max(50).trim())
            .field(User::email, Jod.string().email())
            .field(User::age, Jod.int().min(0).max(120))
            .build();

        // 2. Validate data
        User user = new User("  John Doe  ", "john@example.com", 30);
        
        ValidationResult<User> result = userSchema.validate(user);

        if (result.isValid()) {
            System.out.println("Validation passed!");
            User validatedUser = result.unwrap(); // Returns the transformed/validated object
            System.out.println("Name: " + validatedUser.name()); // "John Doe" (trimmed)
        } else {
            System.out.printf("Validation failed: %s%n", result.getErrors());
        }
    }
}
```

## Core Types
### String Validation
```Java

var s1 = Jod.string().min(5).max(100).email();
var s2 = Jod.string().regex("^[a-zA-Z0-9]+$");
var s3 = Jod.string().url();
var s4 = Jod.string().uuid();

// Transformations
var s5 = Jod.string().trim().toLowerCase();
var s6 = Jod.string().toUpperCase();
```
### Number Validation
``` Java

var n1 = Jod.int().min(0).max(100);
var n2 = Jod.int().positive();
var n3 = Jod.double().negative();
var n4 = Jod.long().multipleOf(5);
```

### Boolean Validation
```Java

var b1 = Jod.bool();
var b2 = Jod.bool().isTrue(); // Enforce value must be true
```

## License
MIT License - see LICENSE file for details.