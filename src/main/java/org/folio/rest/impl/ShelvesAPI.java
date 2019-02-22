package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.folio.rest.jaxrs.model.Shelf;
import org.folio.rest.jaxrs.model.ShelfCollection;
import org.folio.rest.jaxrs.resource.ShelvesResource;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.utils.OutStream;
import org.folio.rest.utils.PgQuery;
import org.folio.rest.utils.PgTransaction;

public class ShelvesAPI implements ShelvesResource {

	private static final Logger logger = LoggerFactory.getLogger(ShelvesAPI.class);

	private final PostgresClient pgClient;
	private static final String[] ALL_FIELDS = { "*" };
	private static final String WAYFINDERS_TABLE = "shelves";

	public ShelvesAPI(Vertx vertx, String tenantId) {
		this.pgClient = PostgresClient.getInstance(vertx, tenantId);
		this.pgClient.setIdField("id");
	}

	@Override
	public void getShelves(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders,
			Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
		logger.info("GET shelves...");
		PgQuery.PgQueryBuilder queryBuilder = new PgQuery.PgQueryBuilder(ALL_FIELDS, WAYFINDERS_TABLE).query(query)
				.offset(offset).limit(limit);
		Future.succeededFuture(queryBuilder)
			.compose(this::runGetQuery)
			.compose(this::parseGetResults)
			.setHandler(asyncResultHandler);
	}

	@Override
	public void postShelves(String lang, Shelf entity, Map<String, String> okapiHeaders,
			Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
		logger.info("POST shelves...");
		PgTransaction<Shelf> pgTransaction = new PgTransaction<Shelf>(entity);
		Future.succeededFuture(pgTransaction)
			.compose(this::startTx)
			.compose(this::saveShelves)
			.compose(this::endTx)
			.compose(this::parsePostResults)
			.setHandler(asyncResultHandler);	
	}

	private Future<PgTransaction<Shelf>> startTx(PgTransaction<Shelf> tx) {
		Future<PgTransaction<Shelf>> future = Future.future();
		pgClient.startTx(sqlConnection -> {
			tx.sqlConnection = sqlConnection;
			future.complete(tx);
		});
		return future;
	}

	private Future<PgTransaction<Shelf>> saveShelves(PgTransaction<Shelf> tx) {
		Future<PgTransaction<Shelf>> future = Future.future();
		try {
			pgClient.save(tx.sqlConnection, WAYFINDERS_TABLE, tx.entity, location -> {
				tx.location = location;
				future.complete(tx);
			});
		} catch (Exception e) {
			future.fail(new Throwable(e));
		}
		return future;
	}

	private Future<PgTransaction<Shelf>> endTx(PgTransaction<Shelf> tx) {
		Future<PgTransaction<Shelf>> future = Future.future();
		pgClient.endTx(tx.sqlConnection, v -> {
			future.complete(tx);
		});
		return future;
	}

	private Future<Response> parsePostResults(PgTransaction<Shelf> tx) {
		final String location = tx.location.result();
		final Shelf shelf = tx.entity;
		shelf.setId(tx.entity.getId());
		OutStream entity = new OutStream();
		entity.setData(shelf);
		Response response = PostShelvesResponse.withJsonCreated(location, entity);
		return Future.succeededFuture(response);
	}	

	private Future<Object[]> runGetQuery(PgQuery.PgQueryBuilder queryBuilder) {
		Future<Object[]> future = Future.future();
		try {
			PgQuery query = queryBuilder.build();
			pgClient.get(query.getTable(), Shelf.class, query.getFields(), query.getCql(), true, false,
					future.completer());
		} catch (Exception e) {
			future.fail(new Throwable(e));
		}
		return future;
	}

	@SuppressWarnings("unchecked")
	public Future<Response> parseGetResults(Object[] resultSet) {
		List<Shelf> shelves = (List<Shelf>) resultSet[0];
		int totalRecords = (Integer) resultSet[1];
		ShelfCollection shelfCollection = new ShelfCollection();
		shelfCollection.setShelves(shelves);
		shelfCollection.setTotalRecords(totalRecords);
		Response response = GetShelvesResponse.withJsonOK(shelfCollection);
		return Future.succeededFuture(response);
	}
}
