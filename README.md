Java Polynomial Class + Interpolation

## Polynomial Class 
- Uses a HashMap data structure for fast lookup during calculations against other `Polynomial`'s, such as multiply, add, or subtract. 
- I overloaded these methods to accept a variety of inputs for them to process the mathematical operations. 
- *For the HashMap, the exponent is the `key` and the coefficient is the `value`*.

## Output
- I created a `toString()` method for the class that prints the polynomial in simplified form sorted by exponent highest to lowest.
- Computationally, this only provides the user a familiar reference to observe the characteristics of the `Polynomial`.
- The `Polynomial Class` is able to return an equation representing *Horner's Form* using `getHornersForm()`.

---

## Divided Difference Table
- Program is able to read any reasonable quantity of (x, fx) values from a *input.txt* file, using `createTableOfValuesFromFile()`, where it extracts the values into corresponding double[] arrays.
- Using this data, the program is able to generate a using Divided Difference Table, saved as a `double[][]` using method `getDividedDifferenceTable()`. 
- Then, using the returned `double[][]`, it can print the table with method `printDividedDifferenceTable()`.
- This will print the table in side-triangle form with corresponding headers of each column.

## Polynomial Interpolation
- Using the tables `double[][]`, I used the `Polynomial Class` to capture the interpolating `Polynomial` for various calculations using method `getPolynomialFromTable()`. - Additionally, `printInterpolatingPolynomialFromTable()` will print the `Polynomial` in it's unsimplified, interpolating form.
- I also coded *Lagrange method*, `getValueFromLagrange()` to interpolate a value for x using the supplied (x, fx) coordinates in the *input.txt* file.

## Demonstration
- The `main()` method provides many explanatory examples of how the `Polynomial Class` and similar methods can be utilized.