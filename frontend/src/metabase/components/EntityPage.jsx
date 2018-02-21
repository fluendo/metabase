import React, { Component } from "react";
import { Box, Flex, Heading } from "rebass";

import { PageLayout, PageSidebar, Wrapper } from "./EntityLayout";

class EntityPage extends Component {
  render() {
    return (
      <div>
        <Box style={{ backgroundColor: "red", minHeight: "65vh" }}>
          Chart goes here
        </Box>
        <Box>
          <Wrapper>
            <Flex>
              <PageLayout>
                <Box my={4}>
                  <Heading>Entity Name</Heading>
                  <p>Description goes here if it exists.</p>
                </Box>
                <Box>
                  <Box my={2}>
                    <h3>What's interesting about this</h3>
                    <p className="text-measure">
                      There is some weirdness in how you have to filter this
                      table in order to get the metric you want. Also note that
                      instances check in twice per day, so if you do a count of
                      rows to determine active instances, make sure to divide it
                      by 2
                    </p>
                  </Box>
                  <Box my={2}>
                    <h3>Things to know about this</h3>
                    <p className="text-measure">
                      There is some weirdness in how you have to filter this
                      table in order to get the metric you want. Also note that
                      instances check in twice per day, so if you do a count of
                      rows to determine active instances, make sure to divide it
                      by 2
                    </p>
                  </Box>
                  <Box my={2}>
                    <h3>How is this calculated?</h3>
                    <p className="text-measure">
                      There is some weirdness in how you have to filter this
                      table in order to get the metric you want. Also note that
                      instances check in twice per day, so if you do a count of
                      rows to determine active instances, make sure to divide it
                      by 2
                    </p>
                  </Box>
                </Box>
              </PageLayout>

              <PageSidebar>
                <Box
                  p={2}
                  mt={4}
                  style={{ border: "1px solid #ddd", borderRadius: 6 }}
                >
                  <Box>
                    <h3>Ways to view this</h3>
                  </Box>
                  <Box>
                    <h3>Segments for this</h3>
                  </Box>
                </Box>
              </PageSidebar>
            </Flex>
          </Wrapper>
        </Box>
      </div>
    );
  }
}

export default EntityPage;
