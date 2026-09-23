## c43SpanishBankProcessor

Reads Spanish bank statements in the **AEB Cuaderno 43** format (also called Norma 43 or C43),
the plain text file most Spanish banks let you download with the account movements.

Work in progress: it reads the movements of the statement, puts each of them in the category
whose definition it matches, and writes the result as a CSV. The Excel part is still to come.

### Usage

```
java -jar target/c43SpanishBankProcessor.jar <C43_FILE> <RESULT_PATH> <DEFINITION_PATH> <LOG_PATH> <DEFAULT_CATEGORY>
```

- **C43_FILE** is the bank statement to read.
- **RESULT_PATH** is the CSV the result is written to (see below).
- **DEFINITION_PATH** is the CSV with the categories (see below).
- **LOG_PATH** is the file where the movements that match no category are written.
- **DEFAULT_CATEGORY** is the category given to the movements that match no category, written as
  `CATEGORY;SUBCATEGORY`, for example `"SIN CLASIFICAR;REVISAR"`. Both parts are mandatory.

Both files are written on every run, replacing what was there before, and their missing
directories are created.

The console says only how it went, and the exit code tells the same to whoever called it:

| Console | Exit code | What happened |
|---|---|---|
| `SUCCESFUL EXECUTION` | `0` | The result file and the log were written |
| `ERROR -> see logs` | `1` | Something failed; the exception and its stack trace are in the log file |
| The problem itself | `2` | The arguments are wrong, so there is no log file to write to |

Internally it builds a `MovementLine` for every movement of the statement, with four fields:

```
year      : 2026
month     : SEPTIEMBRE
importe   : -45.99
plainLine : Registro:22 Movimiento - Clave de oficina origen:0418 - ... - Importe:00000000004599 (45.99) - ... - Registro:23 Concepto complementario - Concepto 1:COMPRA EN CAFETERÍA LA ESPAÑOLA
```

- **plainLine** is the whole movement as one line: the movement record (`22`) followed by the
  concept (`23`) and currency equivalence (`24`) records that complete it. It is a list of
  `name:value` pairs separated by ` - `: the record type, then every informed field (blank fields
  are left out) with its value as written in the file and, for dates, amounts, debit/credit keys,
  currencies and common concepts, what the value means in brackets. A field that is not
  80 characters long adds a warning (`Aviso:...`), and an unknown record shows as
  `Registro:desconocido`.
- **year** and **month** come from the value date (*fecha valor*) of the movement: `260702` gives
  `2026` and `JULIO`.
- **importe** is the amount of the movement as a plain number: `00000000095000 (950.00)` gives
  `950.0`. It is signed after the debit/credit key of the movement: `Clave debe o haber:1` is a
  debit (money out) and gives a negative amount, `2` is a credit (money in) and gives a positive
  one. A zero amount has no sign.

Records that are not movements (account header and footer, end of file) are read and parsed, but
do not become movements. The file is read as ISO-8859-1, the charset banks
use for it.

`MovementLine.matchPattern(definition)` tells whether the movement matches a definition: one or
more patterns separated by `|`, where `*` stands for zero or more characters of any kind and the
rest is literal. The pattern has to match the whole line, and a blank definition matches nothing.

### The categories CSV

Three columns separated by `;`: category, subcategory and the definition that says which
movements belong to them.

```
Alimentacion;Supermercado;*MERCADONA*|*CARREFOUR*
"Ocio;y cultura";Restaurantes;*CAFETERIA*
```

A value may be written between double quotes when it holds a `;`, and inside a quoted value two
double quotes stand for one. Blank lines, lines with less than three columns and lines with any
of the three values empty are ignored; extra columns are ignored too. It is read as windows-1252,
the charset Excel writes CSV files with in Spanish Windows. There is a sample in
`src/test/resources/definitions.csv`.

Every line read becomes a `DefinitionForCategorySubcategory`.

### The result file and the log

Every movement takes the **first** category whose definition it matches, in the order of the
categories CSV. The movements that share year, month, category and subcategory are then added up
into a single line, so the result CSV has five columns: year, month, category, subcategory and
the total amount.

```
2026;JULIO;Ocio;Restaurantes;-50.5
2026;JULIO;Nomina;Empresa;2100.0
```

The first line above holds three movements: two debits of 45.99 and 10.5 and a refund of 5.99.
Amounts are added up with their sign and with `BigDecimal`, so debits subtract, credits add and
the cents are exact. The groups keep the order in which they first appear.

It is written in windows-1252 with `;` as separator, and a value holding a `;` or a double quote
is written between double quotes, so Excel reads it back as it was.

A movement matching no category at all takes the default category given in the command line, so
every movement is in the result, and is written to the log file as well, one line each:

```
NO CATEGORY MATCHES IT, the default one was used: year=2026 month=SEPTIEMBRE importe=45.99 line=Registro:22 Movimiento - ...
```

That way the amounts always add up, and the log tells which movements still need a category.

### The C43 format

Every line has 80 characters and starts with its record type:

| Code | Record |
|---|---|
| `11` | Account header: bank, branch, account, period, opening balance, currency |
| `22` | Movement: dates, concepts, debit/credit, amount, references |
| `23` | Additional concept of the previous movement (up to 5) |
| `24` | Currency equivalence of the previous movement |
| `33` | Account footer: number and totals of debits and credits, closing balance |
| `88` | End of file: number of records |

The positions of every field are in `layout/RecordType.java`. There is a sample file in
`src/test/resources/sample.c43`.

### Build

Requires Java 17 and Maven. It opens directly in IntelliJ as a Maven project.

```
mvn clean package
```

### Design

```
C43SpanishBankProcessor    entry point
Arguments              the paths and the default category of the command line
layout/                RecordType (the C43 records and their fields), FieldDefinition, FieldType
parser/                C43FileReader, C43LineParser, ParsedRecord, ParsedField, FieldInterpreter,
                       MovementGrouper and RecordGroup (a movement with its 23/24 records)
model/                 MovementLine (the bean of a movement), MovementLineExtractor, WildcardPattern,
                       DefinitionForCategorySubcategory, DefinitionCsvReader, CsvLineSplitter
                       MovementCategorizer (movement -> category), Categorization, CategorizedMovement,
                       ResultRowAggregator (adds up the movements of a group), ResultRow
printer/               RecordLineFormatter (the plain line of a movement)
writer/                ResultCsvWriter, LogWriter, CsvLineJoiner, TextFileWriter
```

### Credits

The code of this project has been generated by [Claude](https://claude.ai) (Anthropic).
