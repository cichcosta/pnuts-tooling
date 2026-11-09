# Pnuts Syntax & Patterns for Minimal

The Minimal system utilizes the Pnuts language, a dynamic scripting language built for the Java platform. It is expression-based and tightly integrated with Java. Below are key syntax patterns and advanced capabilities relevant for Minimal development.

## String Manipulation
- Substrings can be extracted using the index notation `[start..end]` or `[index]` for a single character.
  ```cpp
  w = "123456"
  charOne = w[1] // Returns '2'
  nextString = w[1..] // Returns '23456'
  ```

## Data Type Conversion
- Pnuts supports type conversions using Java methods such as `toString()`, `toHexString()`, `toCharArray()`.
- Specific built-in conversion functions are available:
  - `stringToInt()`
  - `stringToDouble()`
  - `stringToBoolean()`
  - `stringToBigDecimal()`

## Variable Scope & Exceptions
- The scope of variables in Pnuts is dynamic.
- The `defined()` function does not identify local variables within functions, only global or higher-scope variables.
- Exception handlers defined with `catch` share the same scope as the variables in the block where they are defined.

## Arrays & Collections
- Arrays can be created dynamically.
  - `createArray(size)`
  - Direct literal instantiation: `[value1, value2]`
- Pnuts fully supports array concatenation and index-based element access.

## Interactive Execution
- Pnuts can be executed in interactive mode or in batch via command line.
- This allows incremental testing and debugging using tools like Pnutool, which supports selective execution of code fragments.

## Additional Conventions
- Avoid auto-completing links, keys, numerical values, etc. randomly, as this often leads to logic errors in scripts.
- Ensure that the string concatenation used for SQL statements correctly applies the appropriate locks (`NOLOCK` for `SELECT`, `ROWLOCK` for `UPDATE`/`DELETE`).
