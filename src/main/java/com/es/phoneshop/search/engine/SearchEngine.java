package com.es.phoneshop.search.engine;

import com.es.phoneshop.search.emuns.SortField;
import com.es.phoneshop.search.emuns.SortOrder;

import java.util.List;

public interface SearchEngine<E> {
    List<E> search();

    List<E> search(SortField field, SortOrder order);

    List<E> search(String query, SortField field, SortOrder order);
}
