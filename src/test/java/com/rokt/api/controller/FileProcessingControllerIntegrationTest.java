package com.rokt.api.controller;

import com.rokt.api.model.FileEntry;
import com.rokt.api.model.FileProcessingRequest;
import com.rokt.api.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.*;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FileProcessingController.class)
public class FileProcessingControllerIntegrationTest {
    private final TestUtil testUtil = new TestUtil();
    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() {
        System.setProperty(testUtil.TESTING_PROPERTY, testUtil.TESTING_PROPERTY_VALUE);
    }

    @After
    public void tearDown() {
        System.clearProperty(testUtil.TESTING_PROPERTY);
    }

    @Test
    public void testInvalidFileExtension() throws Exception {
        FileProcessingRequest request = new FileProcessingRequest(
            "test_file1.csv",
            Instant.now(),
            Instant.now().plus(Duration.ofDays(3))
        );

        ResultActions resultActions = testUtil.performHTTPPost(mvc, request);
        testUtil.assertInvalidResponse(resultActions);
    }

    @Test
    public void testCaseSensitivityOfRequestParameters() throws Exception {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("fileName", "test_file1.txt");
        requestParams.put("FROM", Instant.now().toString());
        requestParams.put("to", Instant.now().plus(Duration.ofDays(3)).toString());

        ResultActions resultActions = mvc.perform(
            post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUtil.asJsonString(requestParams))
            );

        testUtil.assertInvalidResponse(resultActions);
    }

    @Test
    public void testEmptyFile() throws Exception {
        FileProcessingRequest request = new FileProcessingRequest(
            "test_file3.txt",
            Instant.now(),
            Instant.now().plus(Duration.ofDays(3))
        );

        ResultActions resultActions = testUtil.performHTTPPost(mvc, request);
        testUtil.assertInvalidResponse(resultActions);
    }

    @Test
    public void testISO6801Timestamps() throws Exception {
        FileProcessingRequest request = new FileProcessingRequest(
            "test_file5.txt",
            Instant.parse("2000-01-01T11:25:49Z"),
            Instant.parse("2000-01-02T20:59:05Z")
        );

        ResultActions resultActions = testUtil.performHTTPPost(mvc, request);
        testUtil.assertInvalidResponse(resultActions);
    }

    @Test
    public void testHappyPathAndInclusivityOfTimeStamps() throws Exception {
        FileProcessingRequest request = new FileProcessingRequest(
            "test_file1.txt",
            Instant.parse("2000-01-08T13:46:48Z"),
            Instant.parse("2000-01-17T11:26:00Z")
        );

        List<String> textData = Arrays.asList(
            "2000-01-08T13:46:48Z amelie_denesik@christiansen.us 9d03c256-5aa3-4080-850c-87133f8b8c35",
            "2000-01-09T05:05:34Z dulce.mcdermott@smith.uk dbc07dea-0153-4199-ba81-70e9848f6a5a",
            "2000-01-09T11:14:38Z jalon_cole@nader.us 3cfde74d-57a5-432d-9e62-e4ddf01f5f9e",
            "2000-01-10T14:52:25Z jana@zboncak.ca 3a779db3-497a-4e86-9441-7ce3983d6939",
            "2000-01-11T11:38:22Z bettie@heller.name 80832a3c-590c-4c27-9d43-4ee51b6fdb5c",
            "2000-01-12T02:04:09Z cassandre.okeefe@schuppe.ca 22964818-4fd4-4b5f-98f1-19a344fd7542",
            "2000-01-12T21:09:53Z malvina_keeling@hicklekoss.com abac4d13-7948-46dc-8a68-b7a84788b91e",
            "2000-01-13T02:17:48Z efren.hettinger@boyle.com 508f0ccc-3e13-4a86-8211-e774b78426ca",
            "2000-01-14T05:59:19Z olga@aufderhar.biz 1f78aa23-6f25-4462-a5d6-0ebdba3373ab",
            "2000-01-14T16:30:10Z cloyd@greenfelderschaden.biz 5375b2f5-bb1a-4512-8d13-0be5aa2db237",
            "2000-01-15T12:06:15Z esteban@hintzmarks.name 52f646ea-196b-4ec6-8a01-9d84c12d29cd",
            "2000-01-16T07:48:24Z sincere@hahnstehr.biz d39b99f0-1e13-4c6f-8323-c7459bcc7252",
            "2000-01-17T03:13:22Z elyssa.kilback@gaylord.com f2586fbd-9cce-4fb5-ae87-9d0468dd4ef9",
            "2000-01-17T11:26:00Z charity@gislasonhowell.us 8aed121c-36d7-4439-93d8-a6e511e17c6e"
        );

        List<FileEntry> expectedResult = testUtil.buildFileEntries(textData);
        ResultActions resultActions = testUtil.performHTTPPost(mvc, request);
        testUtil.assertValidResponse(resultActions, expectedResult);
    }

    @Test
    public void testEndTimestampOutOfBounds() throws Exception {
        FileProcessingRequest request = new FileProcessingRequest(
            "test_file1.txt",
            Instant.parse("2001-07-11T01:16:34Z"),
            Instant.parse("2001-07-15T17:14:40Z")
        );

        List<String> textData = Arrays.asList(
            "2001-07-11T01:16:34Z sibyl@hoppe.biz 7efa8a37-7cd9-48f7-8f26-7f7a87329499",
            "2001-07-11T04:43:09Z nels@trantow.biz af87a9ab-83c3-465f-a5d4-d0bde99028c7",
            "2001-07-11T13:16:26Z felicia@stroman.name 9a8f2ac8-5a0b-4acd-8fd8-fd9fcbf244c0",
            "2001-07-12T06:01:06Z naomie_lockman@bechtelar.biz 2c2f1ea3-fee5-40b5-a085-92da3845fc4b",
            "2001-07-13T03:28:17Z haleigh@rowedach.biz cf89cd22-3b6b-46a9-aad3-dfdf5b776da3",
            "2001-07-13T18:38:51Z ahmad_cassin@cummingsdamore.ca 11d4ef62-b185-4dfb-87da-1dc4a832e2d0",
            "2001-07-14T17:14:40Z howard@lebsackprosacco.co.uk fc5621fa-212b-4750-8606-7dbc21c94f26"
        );

        List<FileEntry> expectedResult = testUtil.buildFileEntries(textData);
        ResultActions resultActions = testUtil.performHTTPPost(mvc, request);
        testUtil.assertValidResponse(resultActions, expectedResult);
    }

    @Test
    public void testStartTimestampOutOfBounds() throws Exception {
        FileProcessingRequest request = new FileProcessingRequest(
            "test_file1.txt",
            Instant.parse("1999-01-01T17:25:49Z"),
            Instant.parse("2000-01-04T04:55:01Z")
        );

        List<String> textData = Arrays.asList(
            "2000-01-01T17:25:49Z dedric_strosin@adams.co.uk dfad33e7-f734-4f70-af29-c42f2b467142",
            "2000-01-01T23:59:04Z abner@bartolettihills.com b3daf720-6112-4a49-9895-62dda13a2932",
            "2000-01-02T20:59:05Z janis_nienow@johnson.name 1f90471c-adc3-4daa-9a6d-ff9d184b7a61",
            "2000-01-02T21:00:55Z casey.eichmann@hayes.us 56cc8832-9f9d-4dc5-b340-8dabc5107430",
            "2000-01-03T16:13:52Z clotilde@nolanbalistreri.uk 8be575ca-2fa6-43d3-bf69-608b70c8be18",
            "2000-01-04T04:55:01Z shanelle.harris@mayert.name 0aa1ebc7-837d-498e-ad07-f1714f146e08"
        );

        List<FileEntry> expectedResult = testUtil.buildFileEntries(textData);
        ResultActions resultActions = testUtil.performHTTPPost(mvc, request);
        testUtil.assertValidResponse(resultActions, expectedResult);
    }
}
