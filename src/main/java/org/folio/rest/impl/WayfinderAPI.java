package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.rest.jaxrs.model.Wayfinder;
import org.folio.rest.jaxrs.model.WayfinderCollection;
import org.folio.rest.jaxrs.resource.WayfindersResource;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.utils.OutStream;
import org.folio.rest.utils.PgQuery;

public class WayfinderAPI implements WayfindersResource {

	private static final Logger logger = LoggerFactory.getLogger(WayfinderAPI.class);

	private final PostgresClient pgClient;
	private static final String[] ALL_FIELDS = { "*" };
	private static final String WAYFINDERS_TABLE = "wayfinders";

	public WayfinderAPI(Vertx vertx, String tenantId) {
		this.pgClient = PostgresClient.getInstance(vertx, tenantId);
		this.pgClient.setIdField("id");
	}

	@Override
	public void getWayfinders(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders,
			Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
		logger.info("GET wayfinders not implemented...");
		throw new Exception("getWayfinders not implemented.");
	}

	@Override
	public void postWayfinders(String lang, Wayfinder entity, Map<String, String> okapiHeaders,
			Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
		logger.info("POST wayfinders not implemented...");
		throw new Exception("postWayfinders not implemented.");
	}
}
