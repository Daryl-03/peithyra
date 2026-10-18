# Debate module

This document records the module's design choices and their tradeoffs. It describes the current implementation; open questions are listed separately.

## Responsibilities

The module handles debate creation, participant registration, and debate listing. The domain owns participation rules: a user cannot join twice, each side can be taken only once, and participants can only join a debate waiting for an opponent. Adding the second participant starts the debate.
