package ru.demo.util.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * суть данной магии такова
 * 1. получаем корневой екстеншн контекст, который для всего рана, он синглтон
 * 2. пытаемся получить его глобал стор по ключу this.getClass(), но там пусто
 * 3. потому срабатывает лямбда на получение/создание того чего нет
 * 4. в лямбде сначала выполняется beforeAllTest(context)
 * 5. затем методом afterAllTests() возвращаем закрываемый ресурс
 * 6. т.к. мы пытаемся получить рутовый контекст, он создаётся до выполнения всех тестов
 * 7. а т.к. мы кладём в контекст закрываемый ресурс
 * то у него гарантированно вызовется метод close(), при разрушении контекста
 * 8. а т.к. контекст рутовый, то он будет разрушаться по окончании выполнения всех тестов.
 * <p>
 * фишка в том, что мы получаем точки входа и выхода в ран.
 * ровно одну _до_всех_тестов_
 * ровно одну _после_всех_тестов_
 */
public interface SuiteExtension extends BeforeAllCallback {

    @Override
    default void beforeAll(ExtensionContext extensionContext) {
        extensionContext.getRoot()
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(this.getClass(),
                        k -> {
                            beforeAllTests(extensionContext);
                            return (ExtensionContext.Store.CloseableResource) this::afterAllTests;
                        });
    }

    default void beforeAllTests(ExtensionContext context) {
    }

    default void afterAllTests() {
    }
}
