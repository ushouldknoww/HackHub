package it.unicam.hackhub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StaticFrontendSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homePageAndFrontendAssetsAreAvailableWithoutLogin() throws Exception {
        mockMvc.perform(get("/"))
        .andExpect(status().isOk());

        mockMvc.perform(get("/index.html"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string(containsString("Explore")));
        
        mockMvc.perform(get("/app.css"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("site-shell")));

        mockMvc.perform(get("/app.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("loadHackathons")));
    }

    @Test
    void privateApiStillRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
