package com.shortcircuit.app;

import static org.openrewrite.java.Assertions.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

class ShortCircuitTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new ShortCircuit());
    }

    @Test
    void short_circuit() {
        rewriteRun(
                java(
                        """
                                    class SomeClass {
                                        public String should_be_changed() {
                                            if (true | false) {
                                                return "False!";
                                            } else {
                                                return "Something";
                                            }
                                        }

                                        private void should_not_be_changed() {
                                            int a = 25;
                                            int b = 15;
                                            int c = a | b;
                                        }
                                    }
                                """,
                        """
                                    class SomeClass {
                                        public String should_be_changed() {
                                            if (true || false) {
                                                return "False!";
                                            } else {
                                                return "Something";
                                            }
                                        }

                                        private void should_not_be_changed() {
                                            int a = 25;
                                            int b = 15;
                                            int c = a | b;
                                        }
                                    }
                                """));
    }

}
