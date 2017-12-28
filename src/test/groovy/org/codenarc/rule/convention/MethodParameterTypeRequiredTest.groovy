/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.convention

import org.codenarc.rule.GenericAbstractRuleTestCase
import org.junit.Test

/**
 * Tests for MethodParameterTypeRequiredRule
 *
 * @author Marcin Erdmann
 */
class MethodParameterTypeRequiredTest extends GenericAbstractRuleTestCase<MethodParameterTypeRequired> {

    @Test
    void testNoViolations() {
        assertNoViolations '''
            class ValidClass {
                void aMethod(int arg, Object anotherArg) {
                }
            }
        '''
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class InvalidClass {
                def aMethod(dynamicArg) {
                }
            }
        '''

        assertSingleViolation(SOURCE, 3, 'def aMethod(dynamicArg) {', '"dynamicArg" parameter of "aMethod" method is dynamically typed')
    }

    @Test
    void testSingleViolationInConstructor() {
        final SOURCE = '''
            class InvalidClass {
                InvalidClass(dynamicArg) {
                }
            }
        '''

        assertSingleViolation(SOURCE, 3, 'InvalidClass(dynamicArg) {', '"dynamicArg" parameter of "<init>" method is dynamically typed')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            class InvalidClass {
                InvalidClass(def defArg, noTypeArg) {
                }

                void twoArgMethod(def defArg, noTypeArg) {
                }
            }
        '''

        assertViolations(SOURCE,
            [
                lineNumber: 3,
                sourceLineText: 'InvalidClass(def defArg, noTypeArg) {',
                messageText: '"defArg" parameter of "<init>" method is dynamically typed'
            ],
            [
                lineNumber: 3,
                sourceLineText: 'InvalidClass(def defArg, noTypeArg) {',
                messageText: '"noTypeArg" parameter of "<init>" method is dynamically typed'
            ],
            [
                lineNumber: 6,
                sourceLineText: 'void twoArgMethod(def defArg, noTypeArg) {',
                messageText: '"defArg" parameter of "twoArgMethod" method is dynamically typed'
            ],
            [
                lineNumber: 6,
                sourceLineText: 'void twoArgMethod(def defArg, noTypeArg) {',
                messageText: '"noTypeArg" parameter of "twoArgMethod" method is dynamically typed'
            ]
        )
    }

    @Override
    protected MethodParameterTypeRequired createRule() {
        new MethodParameterTypeRequired()
    }
}
