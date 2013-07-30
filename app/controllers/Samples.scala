package controllers

import play.api.mvc.{Flash, Action, Controller}
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import models.Sample
import play.api.Play.current
import play.api.Logger

object Samples extends Controller {

  val log = Logger(this.getClass)

  private val sampleForm: Form[Sample] = Form(
    mapping(
      "name" → nonEmptyText,
      "id" → longNumber
    )(Sample.apply)(Sample.unapply)
  )

  def list = Action { implicit request ⇒
    val samples = Sample.findAll
    Ok(views.html.samples.list(samples))
  }

  def newSample = Action { implicit request ⇒
    val form = if (flash.get("error").isDefined)
      sampleForm.bind(flash.data)
    else
      sampleForm
    Ok(views.html.samples.create(form))
  }

  def save = Action { implicit request ⇒
    log.debug("Save called.")
    val newSampleForm = sampleForm.bindFromRequest()

    log.debug("Form: " + newSampleForm)

    newSampleForm.fold(
      hasErrors = { form ⇒
        Redirect(routes.Samples.newSample()).flashing(Flash(form.data) + ("error" → "Validation error"))
      },
      success = { newSample ⇒
        Sample.add(newSample)
        Redirect(routes.Samples.list()).flashing("success" → "Successfully created new Sample.")
      }
    )
  }
}
