import React, { Component } from "react";
import { Box, Flex } from "rebass";

import { PageSidebar, Wrapper } from "./EntityLayout";

import EntityInfo from "./EntityInfo";

import { loadCard } from "metabase/lib/card";

class EntityPage extends Component {
  state = {
    card: {},
  };

  async componentDidMount() {
    const card = await loadCard(this.props.params.cardId);
    this.setState({ card });
  }

  render() {
    const { card } = this.state;
    return (
      <div>
        <Box
          className="border-bottom"
          style={{ backgroundColor: "#FCFDFD", minHeight: "65vh" }}
        >
          {
            // <Visualization />
          }
        </Box>
        <Box>
          <Wrapper>
            <Flex>
              <EntityInfo entity={card} />
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
