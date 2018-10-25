package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import java.util.Map;
import javax.ws.rs.core.Response;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.folio.rest.jaxrs.model.Shelf;
import org.folio.rest.jaxrs.resource.ShelvesResource;
import org.folio.rest.persist.PostgresClient;

public class ShelvesAPI implements ShelvesResource {

	private static final Logger logger = LoggerFactory.getLogger(ShelvesAPI.class);

	private final PostgresClient pgClient;
	private static final String[] ALL_FIELDS = { "*" };
	private static final String WAYFINDERS_TABLE = "wayfinders";

	public ShelvesAPI(Vertx vertx, String tenantId) {
		this.pgClient = PostgresClient.getInstance(vertx, tenantId);
		this.pgClient.setIdField("id");
	}

	@Override
	public void getShelves(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders,
			Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
		logger.info("GET shelves not implemented...");
		throw new Exception("getShelves not implemented.");
	}

	@Override
	public void postShelves(String lang, Shelf entity, Map<String, String> okapiHeaders,
			Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
		logger.info("POST shelves not implemented...");
		throw new Exception("postShelves not implemented.");
	}
}
