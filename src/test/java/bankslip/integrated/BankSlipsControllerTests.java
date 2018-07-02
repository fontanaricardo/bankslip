package bankslip.integrated;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BankSlipsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldAcceptValidBankSlip() throws Exception {
        this.mockMvc.perform(post("/rest/bankslips")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"due_date\":\"2018-01-01\"," +
                    "\"total_in_cents\":\"100000\"," +
                    "\"customer\":\"Trillian Company\"," +
                    "\"status\":\"PENDING\"}"))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().string("Bankslip created"));
    }

    @Test
    public void noParamShouldReturnDefaultError() throws Exception {
        this.mockMvc.perform(post("/rest/bankslips")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Bankslip not provided in the request body"));
    }

    @Test
    public void invalidDataShouldReturnDefaultError() throws Exception {
        this.mockMvc.perform(post("/rest/bankslips")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"due_date\":\"2018-01-01\"," +
                    "\"customer\":\"Trillian Company\"," +
                    "\"status\":\"PENDING\"}"))
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string("Invalid bankslip provided.The possible reasons are: A field of the provided bankslip was null or with invalid values."));
    }

    @Test
    public void shouldReturnOkStatus() throws Exception {
        this.mockMvc.perform(get("/rest/bankslips"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequestWhenUuidIsInvalid() throws Exception {
        this.mockMvc.perform(get("/rest/bankslips/99999"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Invalid id provided - it must be a valid UUID."));
    }

    @Test
    public void shouldReturnNotFoundWhenIdNotDefined() throws Exception {
        this.mockMvc.perform(get("/rest/bankslips/197972a7-4657-422a-9d85-e0663a3dc549"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Bankslip not found with the specified id."));
    }

    @Test
    public void putShouldReturnNotFoundWhenIdNotExists() throws Exception {
        this.mockMvc.perform(put("/rest/bankslips/197972a7-4657-422a-9d85-e0663a3dc549")
            .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"PAID\"}"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Bankslip not found with the specified id."));
    }
}
