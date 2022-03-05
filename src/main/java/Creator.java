import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasquatch.jsonschemainferrer.*;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Creator {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchemaInferrer inferrer = JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_06)
            .addFormatInferrers(FormatInferrers.email(), FormatInferrers.ip())
            .setAdditionalPropertiesPolicy(AdditionalPropertiesPolicies.notAllowed())
            .setRequiredPolicy(RequiredPolicies.nonNullCommonFields())
            .addEnumExtractors(EnumExtractors.validEnum(java.time.Month.class),
                    EnumExtractors.validEnum(java.time.DayOfWeek.class))
            .build();

    private static final File file = new File("src/main/resources/JSON_sample.json");
    private static final File resultFile = new File("src/main/resources/resultSchema.json");

    public static String readJsonFileToString (String dir) {
        File jsonDataInFile = new File(dir);
        String fileTostring = null;
        try {
            fileTostring = FileUtils.readFileToString(jsonDataInFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            fileTostring = "Error in reading file";
        }
        return  fileTostring;
    }

    public static void main(String[] args) throws IOException {
        String fileString = readJsonFileToString(file.getAbsolutePath());

        if(!fileString.equals("Error in reading file")) {
            final JsonNode sample1 = mapper.readTree(fileString);
            final JsonNode resultForSample = inferrer.inferForSample(sample1);
            String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultForSample);
            BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
            writer.write(pretty);
            writer.close();
            System.out.println(pretty);
        }
        else {
            System.out.println("Sorry, can't read file");
        }
    }
}
