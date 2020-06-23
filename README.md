# Webis UUID Tool

Generator for version 5 (name-based SHA1) UUIDs to identify records in
generated web corpus MapFiles.

## Building the Source Code

Run

```bash
./gradlew build
```

inside the source directory. The generated JAR file will be in `jar/webis-uuid.jar`.

## Example Usage

Command-line usage:
```bash
java -jar jar/webis-uuid.jar clueweb12 clueweb12-0200wb-93-16911
```

API usage:

```java
import de.webis.WebisUUID;

// ...

System.out.println(WebisUUID.generateUUID("clueweb12", "clueweb12-0200wb-93-16911"));
```

Result: `7f476110-58fd-5698-b104-8b29c3ac6d55`.

## Other Languages

The Python standard library comes with UUID5 support out of the box and does not need
this utility. The UUID from the example above can be generated in Python with

```python
import uuid

uuid.uuid5(uuid.NAMESPACE_URL, "clueweb12:clueweb12-0200wb-93-16911")
```

